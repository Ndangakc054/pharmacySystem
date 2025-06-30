const sideMenu = document.querySelector("aside");
const menuBtn = document.querySelector("#menu-btn");
const closeBtn = document.querySelector("#close-btn");
const closeBtn1 = document.querySelector("#close-btn");
const themeToggler = document.querySelector(".theme-toggler");
const themeToggler1 = document.querySelector(".theme-toggler1");
//SHOW SIDEBAR
menuBtn.addEventListener('click', () => {
    sideMenu.style.display = 'block';
})

//CLOSE SIDEBAR
closeBtn.addEventListener('click', () => {
    sideMenu.style.display = 'none';
})
closeBtn1.addEventListener('click', () => {
    sideMenu.style.display = 'none';
})
//CHANGE THEME

themeToggler.addEventListener('click', ()=> {
    document.body.classList.toggle('dark-theme-variables');
    themeToggler.querySelector('span:nth-child(1)').classList.toggle('active');
    themeToggler.querySelector('span:nth-child(2)').classList.toggle('active');

     // Save the current theme to localStorage

     if (document.body.classList.contains('dark-theme-variables')) {
     localStorage.setItem('theme','dark');
     } else {
     localStorage.setItem('theme','light')}
})

// Apply the theme based on localStorage

document.addEventListener('DOMContentLoaded', () => {
//Check if there's a saved theme in localStorage
const savedTheme = localStorage.getItem('theme');
if (savedTheme === 'dark') {
//If dark theme is saved, apply it
document.body.classList.add('dark-theme-variables');
themeToggler.querySelector('span:nth-child(1)').classList.remove('active');
themeToggler.querySelector('span:nth-child(2)').classList.add('active');
} else {
// If light theme is saved or no theme is saved, apply the light theme
        document.body.classList.remove('dark-theme-variables');
        themeToggler.querySelector('span:nth-child(1)').classList.add('active');
        themeToggler.querySelector('span:nth-child(2)').classList.remove('active');
}
});

// Script pour empêcher l'accès à une page après logout via le bouton Précédent
if ( !window.location.href.includes("loginPage")){
    if (window.history && window.history.pushState) {
        window.history.pushState(null, null, window.location.href);
        window.onpopstate = function () {
            window.history.pushState(null, null, window.location.href);
            window.location.reload(); //// Recharge la page pour forcer la nouvelle authentification
        }
    }   
}

//SCRIPT FOR SEARCH-ICON 
function toggleSearch() {
    const searchInput = document.querySelector(".search-input");
    if(searchInput.style.display === "none" || searchInput.style.display === "") {
        searchInput.style.display = "block"; //SHOW THE INPUT
        searchInput.focus(); //Put the focus on the input
    } else {
        searchInput.style.display = "none"; //Hide the input if already displayed
        searchInput.value = "";
        filterTable();
    }
}

//SCRIPT TO FILTER TABLE ROWS BASED ON INPUT IN SEARCH INPUT
function filterTable() {
    let input = document.getElementById("searchInput");
    let filter = input.value.toLowerCase();
    let table = document.getElementById("tableBody");
    let rows = table.getElementsByTagName("tr");

    for(let i = 0; i < rows.length; i++ ){
        let td = rows[i].querySelector(".name"); // Take the “Name” column
        if(td) {
            let txValue = td.textContent || td.innerText;
            if(txValue.toLowerCase().startsWith(filter)) { //filter by starting letter
                rows[i].style.display = "";
            } else {
                rows[i].style.display = "none";
            }
        }
    }
}

//FONCTION POUR AJOUTER UN MEDICAMENT DANS LA FACTURE
function addToInvoice(button) {
    // Récupérer la ligne parente
    const row = button.closest("tr");
    
    // Cloner toute la ligne avec son contenu (y compris le champ caché drugId)
    const newRow = row.cloneNode(true);
    
    // Récupérer les informations du médicament
    const name = newRow.querySelector(".name").textContent;
    const unitPrice = newRow.querySelector("td:nth-child(2)").textContent.trim();
    const drugId = newRow.querySelector('input[name="drugId"]').value;

    // Vérifier si l'article est déjà dans la facture
    const invoiceBody = document.getElementById("invoice-body");
    const existingRow = Array.from(invoiceBody.children).find(
        (r) => r.querySelector(".invoice-name")?.textContent === name
    );

    if (existingRow) {
        // Augmenter la quantité si déjà présent
        const quantityCell = existingRow.querySelector(".invoice-quantity");
        const quantity = parseInt(quantityCell.textContent) + 1;
        quantityCell.textContent = quantity;

        // Mettre à jour le total
        const totalCell = existingRow.querySelector(".invoice-total");
        totalCell.textContent = (quantity * parseFloat(unitPrice)).toFixed(2);
    } else {
        // Créer une nouvelle ligne pour la facture
        const invoiceRow = document.createElement("tr");
        invoiceRow.innerHTML = `
            <td class="invoice-name">${name}</td>
            <td class="invoice-quantity">1</td>
            <td class="invoice-unit-price">${unitPrice}</td>
            <td class="invoice-total">${parseFloat(unitPrice).toFixed(2)}</td>
            <input type="hidden" name="drugId" value="${drugId}">
            <td>
                <span class="cancel-icon" onclick="removeFromInvoiceTable(this)">Rétirer</span>
            </td>
        `;
        invoiceBody.appendChild(invoiceRow);
    }
}

// FONCTION POUR DÉCRÉMENTER OU SUPPRIMER DANS LA FACTURE
function removeFromInvoiceTable(button) {
    const row = button.closest("tr");
    const quantityCell = row.querySelector(".invoice-quantity");
    
    if (!quantityCell) {
        alert("Aucune colonne de quantité détectée dans la facture");
        return;
    }

    let quantity = parseInt(quantityCell.textContent);
    if (quantity > 0) {
        quantity--;
        quantityCell.textContent = quantity;
        
        // Supprimer la ligne si quantité = 0
        if (quantity === 0) {
            row.remove();
        } else {
            // Mettre à jour le total
            const unitPrice = parseFloat(row.querySelector(".invoice-unit-price").textContent);
            row.querySelector(".invoice-total").textContent = (quantity * unitPrice).toFixed(2);
        }
    }
}

// FONCTION POUR DÉCRÉMENTER DEPUIS LE STOCK
function removeFromInvoiceByLeft(button) {
    const row = button.closest("tr");
    const name = row.querySelector(".name").textContent;
    const drugId = row.querySelector('input[name="drugId"]').value;

    // Chercher la ligne correspondante dans la facture
    const invoiceBody = document.getElementById("invoice-body");
    const existingRow = Array.from(invoiceBody.children).find(
        (r) => r.querySelector(".invoice-name")?.textContent === name
    );

    if (!existingRow) {
        alert("Ce médicament n'est pas encore dans la facture");
        return;
    }

    // Décrémenter la quantité dans la facture
    const quantityCell = existingRow.querySelector(".invoice-quantity");
    let quantity = parseInt(quantityCell.textContent);
    
    if (quantity > 0) {
        quantity--;
        quantityCell.textContent = quantity;

        // Si la quantité tombe à 0, on enlève la ligne
        if (quantity === 0) {
            existingRow.remove();
        } else {
            // Mettre à jour le total
            const unitPrice = parseFloat(existingRow.querySelector(".invoice-unit-price").textContent);
            existingRow.querySelector(".invoice-total").textContent = (quantity * unitPrice).toFixed(2); 
        }
    }
}
//VALIDER UNE VENTE
document.getElementById('validate-invoice').addEventListener('click', function(event) {
    event.preventDefault();
    const invoiceBody = document.getElementById('invoice-body');
    const rows = invoiceBody.getElementsByTagName('tr');
    //Vérifier qeu la facture est vide
    if(rows.length === 0) {
        alert("Impossible de valider : la facture est vide !");
        return;
    }
    const items = [];

    for (let row of rows) {
        const name = row.querySelector('.invoice-name').textContent;
        const quantity = parseInt(row.querySelector('.invoice-quantity').textContent);
        const unitPrice = parseFloat(row.querySelector('.invoice-unit-price').textContent);
        const totalAmount = quantity * unitPrice;
        //const totalAmount = parseFloat(row.querySelector('.invoice-total').textContent);

        items.push({
            idDrug: parseInt(row.querySelector('input[name="drugId"]').value),
            drugName : name,
            quantity: quantity,
            unitPrice: unitPrice,
            totalAmount: totalAmount
        });
    }
    // Récupération et conversion de userId en nombre entier
    const userId = parseInt(document.getElementById('user-id').value);
    const userName = document.getElementById('user-name').textContent;


    const salesForm = {
        //userId: document.getElementById('user-id').textContent,
        userId: userId,
        userName: userName,
        dateVente: new Date().toISOString(),
        items: items
    };

    fetch('/sales/save', {
        method: 'POST',
        headers: {
            'Content-Type' : 'application/json',
        },
        body: JSON.stringify(salesForm),
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => { throw new Error(text) });
            //throw new Error("Erreur HTTP : " + response.status);
        }
        return response.json();
    })
    .then(data => {
        console.log('Success:', data);
        alert(data.message || "Facture validée avec succès !");

        // Vider le tableau après la validation
        invoiceBody.innerHTML = '';
        
        // Ouvrir le PDF dans un nouvel onglet

        if (data.pdfUrl) {
            const link = document.createElement('a');
            link.href = data.pdfUrl;
            link.target = '_blank';
            link.click(); // Déclenche un clic sur le lien
        }
    })
    .catch((error) => {
        console.error('Error:', error);
        alert('Erreur: ' + error.message);
        //''alert('Erreur lors de le validation de la  putain de facture.');
    }) ;
});
//calculer PrixTotal automatiquement lors de la soumission du formulaire de modification de la facture 
document.addEventListener("input", function (event) {
    if(event.target.name.includes("quantity") || event.target.name.includes("unitPrice")) {
        const row = event.target.closest("tr");
        const quantity = row.querySelector('input[name*="quantity"]').value;
        const unitPrice = row.querySelector('input[name*="unitPrice"]').value;
        const totalAmount = (quantity * unitPrice).toFixed(2);
        row.querySelector('input[name*="totalAmount"]').value = totalAmount;
    }
});
// Formattage du JSON dans la page d'historique
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('pre.json').forEach(el => {
        try {
            // Remplace d'abord les &quot; par des guillemets normaux
            let rawContent = el.innerHTML.replace(/&quot;/g, '"');
            
            // Essaye de parser le JSON
            const json = JSON.parse(rawContent);
            
            // Formate avec indentation
            el.innerHTML = JSON.stringify(json, null, 2)
                .replace(/</g, '&lt;')  // Échappe les < pour la sécurité
                .replace(/>/g, '&gt;'); // Échappe les > pour la sécurité
        } catch(e) {
            console.error('Erreur de parsing JSON', e);
            // Affiche le contenu brut si le parsing échoue
            el.innerHTML = el.innerHTML.replace(/&quot;/g, '"');
        }
    });
});





