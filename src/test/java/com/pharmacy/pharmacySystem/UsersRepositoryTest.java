package com.pharmacy.pharmacySystem;

import com.pharmacy.pharmacySystem.user.Users;
import com.pharmacy.pharmacySystem.user.UsersRepository;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import javax.naming.InsufficientResourcesException;
import java.util.List;
import java.util.Optional;

//@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
@Rollback(false)
public class UsersRepositoryTest {
    @Autowired private UsersRepository repo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Test
    public void testAddNew() {
        PasswordEncoder passwordEncoder1 = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder1.encode("ndanga4554");
        System.out.println("Hashed password length: "+hashedPassword.length());
        Users user = new Users();
        user.setNomUtilisateur("Linuxer31");
        user.setMotDePasse(hashedPassword);
        user.setType("ADMIN");

        Users saveUser = repo.save(user);

        Assertions.assertThat(saveUser).isNotNull();
        Assertions.assertThat(saveUser.getId()).isGreaterThan(0);
    }
    @Test
    public void testListAll() {
        Iterable<Users> users = repo.findAll();
        Assertions.assertThat(users).hasSizeGreaterThan(0);
        for (Users user : users) {
            System.out.println(user);
        }
    }
    @Test
    public void testUpdate() {
        Integer userId = 1;
        Optional<Users> optionalUsers = repo.findById(userId);
        Users user = optionalUsers.get();
        user.setType("ADMIN");
        repo.save(user);
        Users updateUsers = repo.findById(userId).get();
        Assertions.assertThat("ADMIN");
    }
    @Test
    public void  testEncodeExistingMotDePasse() {
        List<Users> users = (List<Users>) repo.findAll();
        for (Users user: users) {
            if (!user.getMotDePasse().startsWith("$2a$")) {
                user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
                repo.save(user);
            }
        }
    }
    @Configuration class PasswordEncoderConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}
