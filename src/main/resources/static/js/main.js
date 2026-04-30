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

//Code Js pour le modal du message après un ajout, une modification ou une suppression
document.addEventListener("DOMContentLoaded", function() {

    // Vérifie si la modal existe
    const modal = document.getElementById('notification-modal');

    if (modal) {
        // --- 1. Définition de la fonction de fermeture ---
        const closeModal = () => {
            // Ajoute l'animation de sortie définie dans votre CSS
            modal.style.animation = 'slideOut 0.5s forwards';

            // Attend la fin de l'animation (500ms) avant de supprimer l'élément
            setTimeout(() => {
                modal.remove();
            }, 500);
        };

        // --- 2. Gérer le bouton "Fermer" (le X) ---
        // On cherche le bouton à l'intérieur du modal
        const closeBtn = modal.querySelector('.close-btn');
        if (closeBtn) {
            // On attache l'événement click ici au lieu du HTML
            closeBtn.addEventListener('click', closeModal);
        }

        // --- 3. Gérer la fermeture automatique (Timer) ---
        const displayTime = 10000; // 10 secondes
        setTimeout(() => {
            // Vérifie si le modal existe encore avant d'essayer de le fermer
            if (document.body.contains(modal)) {
                closeModal();
            }
        }, displayTime);
    }
});

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
/**function filterTable() {
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
}**/

//SCRIPT TO FILTER TABLE ROWS BASED ON INPUT IN SEARCH INPUT IN ORDERS PAGE
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

//SCRIPT POUR LE  FILTRE DE LA PAGE D'EXPIRATION
function filterByStatus() {
    // 1. Récupérer la valeur sélectionnée (URGENT, ATTENTION, NORMAL ou all)
    var select = document.getElementById("statusSelect");
    var filter = select.value.toUpperCase();

    // 2. Récupérer toutes les lignes du tableau
    var tableBody = document.getElementById("tableBody");
    var rows = tableBody.getElementsByTagName("tr");

    // 3. Parcourir les lignes pour filtrer
    for (var i = 0; i < rows.length; i++) {
        // Le statut se trouve dans la 5ème colonne (index 4)
        var statusCell = rows[i].getElementsByTagName("td")[4];

        if (statusCell) {
            var statusText = (statusCell.textContent || statusCell.innerText).toUpperCase();

            // LOGIQUE :
            // Si on choisit "all", on affiche tout.
            // Sinon, on affiche seulement si le texte de la cellule contient le filtre.
            if (filter === "ALL" || statusText.includes(filter)) {
                rows[i].style.display = ""; // Affiche la ligne
            } else {
                rows[i].style.display = "none"; // Cache la ligne
            }
        }
    }
}
//SCRIPT POUR CALCULER LE MONTANT TOTAL À PAYER DANS LA FACTURE

function updateGrandTotal() {
    const invoiceBody = document.getElementById("invoice-body");
    const rows = invoiceBody.querySelectorAll("tr");
    let totalGeneral = 0;

    rows.forEach(row => {
        const totalCell = row.querySelector(".invoice-total");
        if (totalCell) {
            totalGeneral += parseFloat(totalCell.textContent);
        }
    });

    // Mettre à jour l'affichage dans le tfoot
    document.getElementById("grand-total").textContent = totalGeneral.toFixed(2);
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
    updateGrandTotal();
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
    updateGrandTotal();
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
    updateGrandTotal();
}

//VALIDER UNE VENTE

// VALIDER UNE VENTE
const validateBtn = document.getElementById('validate-invoice');
if (validateBtn) { // On vérifie si le bouton existe d'abord
    validateBtn.addEventListener('click', function(event) {
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
}

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

document.addEventListener("DOMContentLoaded", function() {
    const profileTrigger = document.getElementById('profileTrigger');
    const userPopup = document.getElementById('user-popup');

    if (profileTrigger && userPopup) {
        // Toggle (ouvrir/fermer) au clic sur le profil
        profileTrigger.addEventListener('click', (e) => {
            e.stopPropagation(); // Empêche la fermeture immédiate
            const isVisible = userPopup.style.display === 'block';
            userPopup.style.display = isVisible ? 'none' : 'block';
        });

        // Fermer si on clique n'importe où ailleurs sur la page
        document.addEventListener('click', (e) => {
            if (!profileTrigger.contains(e.target)) {
                userPopup.style.display = 'none';
            }
        });
    }
});

// 1. AJOUTER OU INCRÉMENTER DANS LA FICHE DE REQUISITION
function addToPurchaseList(button) {
    const row = button.closest("tr");
    const nameCell = row.querySelector(".name") || row.querySelector(".order-name");
    const name = nameCell.textContent.trim();

    const ordersBody = document.getElementById("orders-body");

    const existingRow = Array.from(ordersBody.children).find(
        (r) => r.querySelector(".order-name")?.textContent === name
    );

    if (existingRow) {
        const qtySpan = existingRow.querySelector(".order-quantity");
        qtySpan.textContent = parseInt(qtySpan.textContent) + 1;
    } else {
        const newRow = document.createElement("tr");
        newRow.innerHTML = `
            <td class="order-name">${name}</td>
            <td class="order-quantity">1</td>
            <td>
               <span class="add-icon" onclick="addToPurchaseList(this)"><img src="/imgs/add_circle.svg"></span>
            </td>
            <td>
               <span class="cancel-icon" onclick="removeFromPurchaseListByLeft(this)"><img src="/imgs/remove_circle.svg"></span>
            </td>
        `;
        ordersBody.appendChild(newRow);
    }
}

// 2. DÉCRÉMENTER OU SUPPRIMER DANS LA FICHE DE RÉQUISITION
function removeFromPurchaseListByLeft(button) {
    const row = button.closest("tr");
    const name = (row.querySelector(".name") || row.querySelector(".order-name")).textContent.trim();
    const ordersBody = document.getElementById("orders-body");

    // Chercher la ligne dans la fiche de réquisition
    const targetRow = Array.from(ordersBody.children).find(
        (r) => r.querySelector(".order-name")?.textContent === name
    );

    if (targetRow) {
        const qtySpan = targetRow.querySelector(".order-quantity");
        let qty = parseInt(qtySpan.textContent);

        if (qty > 1) {
            qtySpan.textContent = qty - 1;
        } else {
            // Si la quantité arrive à 1 et qu'on clique encore, on supprime la ligne
            targetRow.remove();
        }
    }
}
/** == FONCTION POUR GÉNÉRER LE PDF DU FICHE DE REQUISITION == **/

document.getElementById('validate-file').addEventListener('click', function(event) {
    event.preventDefault();

    const ordersBody = document.getElementById('orders-body');
    const rows = ordersBody.getElementsByTagName('tr');

    if (rows.length === 0) {
        alert("La fiche est vide !");
        return;
    }

    // 1. On récupère les données du tableau
    const items = [];
    for (let row of rows) {
        items.push({
            drugName: row.querySelector('.order-name').textContent,
            quantity: parseInt(row.querySelector('.order-quantity').textContent)
        });
    }

    // 2. Envoi au serveur
    fetch('/requisition/generate-pdf', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(items)
    })
    .then(response => {
        if (!response.ok) throw new Error("Erreur serveur");
        return response.blob(); // On récupère le fichier PDF
    })
    .then(blob => {
        // 3. Création d'une URL pour le fichier et ouverture dans un nouvel onglet
        const fileURL = URL.createObjectURL(blob);
        window.open(fileURL, '_blank');
    })
    .catch(error => {
        console.error('Erreur:', error);
        alert("Impossible de générer le PDF");
    });
});
//SCRIPT POUR LE TABLEAU DES VENTES

function filterByDate() {
    var dateInput = document.getElementById("datePicker");
    var date = dateInput.value;

    if (date) {
        // Cette méthode récupère l'URL actuelle (ex: /purchases ou /recentSales)
        // et recharge la page avec le nouveau paramètre date
        var currentPath = window.location.pathname;
        window.location.href = currentPath + "?date=" + date;
    }
}
// MODAL POUR LE USER POUR ÉDITER SON PROFIL
document.addEventListener("DOMContentLoaded", function() {
    const profileTrigger = document.getElementById('profileTrigger');
    const userPopup = document.getElementById('user-popup');

    if (profileTrigger && userPopup) {
        // Toggle (ouvrir/fermer) au clic sur le profil
        profileTrigger.addEventListener('click', (e) => {
            e.stopPropagation(); // Empêche la fermeture immédiate
            const isVisible = userPopup.style.display === 'block';
            userPopup.style.display = isVisible ? 'none' : 'block';
        });

        // Fermer si on clique n'importe où ailleurs sur la page
        document.addEventListener('click', (e) => {
            if (!profileTrigger.contains(e.target)) {
                userPopup.style.display = 'none';
            }
        });
    }
});

//SCRIPT POUR LE  FILTRE DE LA PAGE DRUGS
function filterByCategory() {
    var select = document.getElementById("categorySelect");
    var filter = select.value.toUpperCase();
    var tableBody = document.getElementById("tableBody");
    var rows = tableBody.getElementsByTagName("tr");

    for (var i = 0; i < rows.length; i++) {
        // CHANGEMENT ICI : Index 1 pour la colonne Catégorie
        var categoryCell = rows[i].getElementsByTagName("td")[1];

        if (categoryCell) {
            var categoryText = (categoryCell.textContent || categoryCell.innerText).toUpperCase();

            if (filter === "ALL" || categoryText.includes(filter)) {
                rows[i].style.display = "";
            } else {
                rows[i].style.display = "none";
            }
        }
    }
}
// --- FONCTION DE FERMETURE (Mise en dehors pour être accessible partout) ---


/**document.addEventListener("DOMContentLoaded", function() {
    const modal = document.getElementById('notification-modal');

    if (modal) {
        const closeModal = () => {
            // 1. On lance l'animation
            modal.style.animation = 'slideOut 0.5s forwards';

            // 2. Par sécurité, on réduit l'opacité immédiatement
            modal.style.opacity = '0';
            modal.style.transition = 'opacity 0.5s ease';

            // 3. Suppression physique après l'animation
            setTimeout(() => {
                if (modal && modal.parentNode) {
                    modal.style.display = 'none'; // Cache immédiatement
                    modal.remove(); // Supprime du DOM
                }
            }, 500);
        };

        const closeBtn = modal.querySelector('.close-btn');
        if (closeBtn) {
            closeBtn.onclick = closeModal; // Utilisation de onclick pour écraser tout conflit
        }

        // Fermeture automatique après 10s
        setTimeout(() => {
            if (document.body.contains(modal)) {
                closeModal();
            }
        }, 10000);
    }
});**/



