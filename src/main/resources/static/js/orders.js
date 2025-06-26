const Orders = [
    {
        productName: 'Foldable Mini Drone',
        productNumber: '85631',
        paymentStatus: 'Due',
        shipping: 'Pending'
    },
    {
        productName: 'LARVENDER KF102 Drone',
        productNumber: '36378',
        paymentStatus: 'Refunded',
        shipping: 'Declined'
    },
    {
        productName: 'Ruko F11 Pro Drone',
        productNumber: '49347',
        paymentStatus: 'Due',
        shipping: 'Pending'
    },
    {
        productName: 'Drone with Camera Drone',
        productNumber: '96996',
        paymentStatus: 'Paid',
        shipping: 'Delivered'
    },
    {
        productName: 'GPS 4k Drone',
        productNumber: '22821',
        paymentStatus: 'Paid',
        shipping: 'Delivered'
    },
    {
        productName: 'DJI Air 25',
        productNumber: '81475',
        paymentStatus: 'Due',
        shipping: 'Pending'
    },
    {
        productName: 'Lozenge Drone',
        productNumber: '00482',
        paymentStatus: 'Paid',
        shipping: 'Delivered'
    }
]
document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById("notificationModal");
    const closeModal = document.getElementById("closeModal");
    const message = document.getElementById("modalMessage");

    // Gestion des données flash
    const flashData = document.getElementById('flash-data');
    if (flashData) {
        const status = flashData.getAttribute('data-status');
        const modalMessage = flashData.getAttribute('data-message');

        if (status && modalMessage && modalMessage.trim() !== "") {
            message.textContent = modalMessage;

            // Changez la couleur ou le style de la modale selon le statut
            if (status === "success") {
                modal.classList.remove("error"); // Supprimez la classe d'erreur si elle existe
                modal.classList.add("success"); // Ajoutez une classe pour le succès
            } else if (status === "error") {
                modal.classList.remove("success"); // Supprimez la classe de succès si elle existe
                modal.classList.add("error"); // Ajoutez une classe pour l'erreur
            }

            modal.style.display = 'block';
        }
    }

    // Fermer la modale
    closeModal.onclick = function() {
        modal.style.display = 'none';
    };
   
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    };

    // Validation du formulaire
    const form = document.querySelector(".drug-form form");
    form.addEventListener("submit", function(event) {
        const drugCode = document.getElementById("drugCode").value;
        const drugName = document.getElementById("drugName").value;
        const drugCategorie = document.getElementById("drugCategorie").value;
        const drugMeasure = document.getElementById("drugMeasure").value;
        const drugQuantity = document.getElementById("drugQuantity").value;
        const drugUnitPrice = document.getElementById("drugUnitPrice").value;
        const drugDate = document.getElementById("drugDate").value;

        if (!drugCode || drugCode.length > 5 ||
            !drugName || drugName.length > 45 ||
            !drugCategorie ||
            !drugMeasure ||
            !drugQuantity || isNaN(drugQuantity) || drugQuantity <= 0 || 
            !drugUnitPrice || isNaN(drugUnitPrice) || drugUnitPrice <= 0 ||
            !drugDate) {
                event.preventDefault(); 
                message.textContent = "Certains champs sont vides ou contiennent des données invalides. Veuillez vérifier vos saisies.";
                modal.classList.remove("success");
                modal.classList.add("error");
                modal.style.display = "block";
        }
    });
});
/********************************************************************************************************** */
/*********************                    *****************************************     *********************/
document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById("odal");
    const closeModal = document.getElementById("closeModal");
    const message = document.getElementById("modalMessage");

    
    const flashData = document.getElementById('flash-data');
    console.log(flashData);
    if (flashData) {
        const status = flashData.getAttribute('data-status');
        const modalMessage = flashData.getAttribute('data-message');
        console.log("Status:", status);
        console.log("Message:", modalMessage);
          
           if (status && modalMessage && modalMessage.trim() !== "") {
            message.textContent = modalMessage;
            
            if(status === "success") {
                modal.classList.remove("error");
                modal.classList.add("success");
            } else if (status === "error") {
                modal.classList.remove("success");
                modal.classList.add("error");
            }
            modal.style.display = 'block';
           }
    }
   
    closeModal.onclick = function() {
        modal.style.display = 'none';
    };
   
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none"
        }
    };

    const form = document.querySelector(".drug-form form");
    form.addEventListener("submit", function(event) {
        const drugCode = document.getElementById("drugCode").value;
        const drugName = document.getElementById("drugName").value;
        const drugCategorie = document.getElementById("drugCategorie").value;
        const drugMeasure = document.getElementById("drugMeasure").value;
        const drugQuantity = document.getElementById("drugQuantity").value;
        const drugUnitPrice = document.getElementById("drugUnitPrice").value;
        const drugDate = document.getElementById("drugDate").value;

        
        if(!drugCode || drugCode.length > 5 ||
            !drugName || drugName.length > 45 ||
             !drugCategorie || drugCategorie > 10 ||
              !drugMeasure || drugMeasure > 10 ||
               !drugQuantity || isNaN(drugQuantity || drugQuantity <= 0 || 
                !drugUnitPrice || isNaN(drugUnitPrice) || drugUnitPrice <= 0 ||
                 !drugDate)) {
                    event.preventDefault(); 
                    message.textContent = "Certains champs sont vides ou contiennent des données invalides. Veuillez vérifier vos saisies.";
                    modal.classList.remove("success");
                    modal.classList.add("error");
                    modal.style.display = "block"
                 }
    });

});


    /**const status = modal.getAttribute("data-status");
    const message = modal.getAttribute("data-message");
    
    if (status !== "null" && message.trim() !== "") {
        modal.style.display = "block";
        modalMessage = message;
        modalMessage.parentNode.classList.add(status === "success" ? "success" : "error");
    }

    closeModal.onclick = function () {
        modal.style.display = "none";
    };

    window.onclick = function (event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    };**/












    document.addEventListener("DOMContentLoaded", function () {
        const form = document.getElementById("drugForm");
        const modal = document.getElementById("modal");
        const closeModalBtn = document.getElementById("close-modal");
        const modalMessage = document.getElementById("modal-message");
    
        // Événement de soumission du formulaire
        form.addEventListener("submit", function (event) {
            event.preventDefault(); // Empêche le rechargement immédiat de la page
    
            // Simulation d'un message flash depuis le serveur
            const status = modal.getAttribute("data-status");
            const message = modal.getAttribute("data-message");
    
            // Afficher le modal uniquement après le clic sur Submit
            if (status && message) {
                modal.style.display = "block";
                modalMessage.textContent = message;
    
                // Ajouter classe pour style (success ou error)
                modal.querySelector(".modal-content").classList.add(status === "success" ? "success" : "error");
            }
        });
    
        // Fermer le modal
        closeModalBtn.addEventListener("click", function () {
            modal.style.display = "none";
        });
    
        // Fermer le modal en cliquant à l'extérieur
        window.addEventListener("click", function (event) {
            if (event.target === modal) {
                modal.style.display = "none";
            }
        });
    });


    
    