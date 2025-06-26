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
/**function addToInvoice(button) {
    //Recuperer la ligne parente
    const row = button.closest("tr");
    //Récupérer l'ID
    const drugId = row.dataset.drugId;

    //Recuperer les informations du m"dicament
    const name = row.querySelector(".stock-name").textContent;
    const unitPrice = row.children[1].textContent.trim();

    //Vérifier si l'article est déja dans la facture
    const invoiceBody = document.getElementById("invoice-body");
    const existingRow = Array.from(invoiceBody.children).find(
        (r) => r.querySelector(".invoice-name")?.textContent === name
    );
    if (existingRow) {
        //Augmenter la quantité si déja present
        const quantityCell = existingRow.querySelector(".invoice-quantity");
        const quantity = parseInt(quantityCell.textContent) +1;
        quantityCell.textContent = quantity;

        //Mettre à jour le total
        const totalCell = existingRow.querySelector(".invoice-total");
        totalCell.textContent = (quantity * parseFloat(unitPrice)).toFixed(2);
    } else {
        //Ajouter une nouvelle ligne si non présent 
        const newRow = document.createElement("tr");
        newRow.innerHTML = `
            <td class="invoice-name">${name}</td>
            <td class="invoice-quantity">1</td>
            <td class="invoice-unit-price">${unitPrice}</td>
            <td class="invoice-total">${parseFloat(unitPrice).toFixed(2)}</td>
            <input type="hidden" name="drugId" value="${drugId}">
            <td>
                <span class="material-icons-sharp cancel-icon" onclick="removeFromInvoiceTable(this)">highlight_off</span>
            </td>
        `;
        invoiceBody.appendChild(newRow);
    }
}

//Décrémenter quand on clique dans la FACTURE elle-même
function removeFromInvoiceTable(button) {
    // Ici, le "button" est dans la facture (tableau de droite)
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
      
      // Supprime la ligne si quantité = 0
      if (quantity === 0) {
        row.remove();
      } else {
        // Mettre à jour le total
        const unitPrice = parseFloat(row.querySelector(".invoice-unit-price").textContent);
        row.querySelector(".invoice-total").textContent = (quantity * unitPrice).toFixed(2);
      }
    }
  }
  //FONCTION POUR DECREMENTER LA QUANTITÉ OU SUPPRESSION DE LA LIGNE DANS LA FACTURE
  
  function removeFromInvoiceByLeft(button) {
    const row = button.closest("tr");
    //Get the drug name
    const name = row.querySelector(".stock-name").textContent;

    //cherchez la ligne correspondante dans la facture
    const invoiceBody = document.getElementById("invoice-body");
    const existingRow = Array.from(invoiceBody.children).find(
        (r) => r.querySelector(".invoice-name")?.textContent === name
    );

    if (!existingRow) {
        alert("Ce medicament n'est pas encore dans la facture");
        return;
    }

    //DECREMENTER LA QUANTITÉ DANS LA FACTURE
    const quantityCell = existingRow.querySelector(".invoice-quantity");
    let quantity = parseInt(quantityCell.textContent);
    if (quantity > 0) {
        quantity--;
        quantityCell.textContent = quantity;

        //si la quantité tombe à 0, on enlève la ligne
        if(quantity === 0) {
            existingRow.remove();
        } else {
            //Mettre à jour le total 
            const unitPrice = parseFloat(existingRow.querySelector(".invoice-unit-price").textContent);
            existingRow.querySelector(".invoice-total").textContent = (quantity * unitPrice).toFixed(2); 
        }
    }
}**/
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
/*********************************************/

//FONCTION POUR AJOUTER UN MEDICAMENT DANS LE LISTE D'ACHAT
function addToPurchaseList(button) {
    //Recuperer la ligne parente
    const row = button.closest("tr");

    //Recuperer les informations du m"dicament
    const name = row.querySelector(".name").textContent;
    const unitPrice = row.children[1].textContent.trim();

    //Vérifier si l'article est déja dans la facture
    const ordersBody = document.getElementById("orders-body");
    const existingRow = Array.from(ordersBody.children).find(
        (r) => r.querySelector(".orders-name")?.textContent === name
    );
    if (existingRow) {
        //Augmenter la quantité si déja present
        const quantityCell = existingRow.querySelector(".orders-quantity");
        const quantity = parseInt(quantityCell.textContent) +1;
        quantityCell.textContent = quantity;
    } else {
        //Ajouter une nouvelle ligne si non présent 
        const newRow = document.createElement("tr");
        newRow.innerHTML = `
            <td class="orders-name">${name}</td>
            <td class="orders-quantity">1</td>
            <td>
                <span class="cancel-icon" onclick="removeFromPurchaseListTable(this)">highlight_off</span>
            </td>
        `;
        ordersBody.appendChild(newRow);
    }
}

//Décrémenter quand on clique dans la liste des médicaments à acheter
function removeFromPurchaseListTable(button) {
    // Ici, le "button" est dans la facture (tableau de droite)
    const row = button.closest("tr");
  
    const quantityCell = row.querySelector(".orders-quantity");
    if (!quantityCell) {
      alert("Aucune colonne de quantité détectée");
      return;
    }
  
    let quantity = parseInt(quantityCell.textContent);
    if (quantity > 0) {
      quantity--;
      quantityCell.textContent = quantity;
      
      // Supprime la ligne si quantité = 0
      if (quantity === 0) {
        row.remove();
      } 
    }
  }
  //FONCTION POUR DECREMENTER LA QUANTITÉ OU SUPPRESSION DE LA LIGNE DANS LA LISTE DES MEDICAMENTS À ACHETER
  
  function removeFromPurchaseListByLeft(button) {
    const row = button.closest("tr");
    //Get the drug name
    const name = row.querySelector(".stock-name").textContent;

    //cherchez la ligne correspondante dans la facture
    const invoiceBody = document.getElementById("orders-body");
    const existingRow = Array.from(invoiceBody.children).find(
        (r) => r.querySelector(".orders-name")?.textContent === name
    );
    if (!existingRow) {
        alert("Ce medicament n'est pas encore dans la liste");
        return;
    }

    //DECREMENTER LA QUANTITÉ DANS LA FICHE DE REQUISITION
    const quantityCell = existingRow.querySelector(".orders-quantity");
    let quantity = parseInt(quantityCell.textContent);
    if (quantity > 0) {
        quantity--;
        quantityCell.textContent = quantity;

        //si la quantité tombe à 0, on enlève la ligne
        if(quantity === 0) {
            existingRow.remove();
        }
    }
}
/************************************************************************ */


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

/**function refreshOrders() {
    fetch('/refreshOrders', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
    })
    .then(response => response.json())
    .then(data => {
        const tableBody = document.getElementById('stockTableBody');
        tableBody.innerHTML = ''; // Vider le tableau

        data.forEach(stock => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td class="stock-name">${stock.drug.nameDrug}</td>
                <td>${stock.quantity}</td> <!-- Assurez-vous que le nom correspond à la propriété Java -->
                <td>
                    <span class="material-icons-sharp add-icon" onclick="addToInvoice(this)">add_circle_outline</span>
                </td>
                <td>
                    <span class="material-icons-sharp cancel-icon" onclick="removeFromInvoiceByLeft(this)">highlight_off</span>
                </td>
            `;
            tableBody.appendChild(row);
        });
    })
    .catch(error => {
        console.error('Erreur', error);
    });
}**/
//VALIDER UNE FICHE DE COMMANDE


document.getElementById('validate-file').addEventListener('click', function() {
    const items = [];
    const rows = document.querySelectorAll('#orders-body tr');
    
    rows.forEach(row => {
        const name = row.querySelector('.orders-name').textContent;
        const quantity = parseInt(row.querySelector('.orders-quantity').textContent);
        
        items.push({
            drugName: name,
            quantity: quantity
        });
    });

    const formData = {
        dateOrders: new Date().toISOString().split('T')[0], // Date du jour au format YYYY-MM-DD
        items: items
    };

    // Envoyer les données au backend
    fetch('/orders/save', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.pdfUrl) {
            // Ouvrir le PDF dans un nouvel onglet
            window.open(data.pdfUrl, '_blank');
        }
    })
    .catch(error => console.error('Error:', error));
});

/**document.getElementById('validate-file').addEventListener('click', async () => {
    const ordersForm = {
        userId: document.getElementById('userId').textContent,
        dateOrders: new Date().toISOString().split('T')[0],
        items: []
    };
    // Récupérer les médicaments du tableau
    document.querySelectorAll('#orders-body tr').forEach(row => {
        const drugName = row.querySelector('.orders-name').textContent;
        const quantity = parseInt(row.querySelector('.orders-quantity').textContent);

        ordersForm.items.push({
            drugName: drugName,
            minThreshold: quantity
        });
    });
    try {
        const validateBtn = document.getElementById('validate-file');
        validateBtn.disabled = true; // Désactiver le bouton pendant l'envoi

        const response = await fetch('/orders/save', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(ordersForm)
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Erreur HTTP: ${response.status} - ${errorText}`);
        }

        const result = await response.json();

        //Ouvrir le PDF dans un nouvel onglet
        window.open('/orders/pdf/${result.pdfUrl.split('/').pop()}', '_blank');
    } catch (error) {
        console.error('Erreur:',error);
        alert('Echec de la validation : ' + error.message);
    } finally {
        validateBtn.disabled = false;
    }
});***/

/**document.getElementById('validate-file').addEventListener('click', async () => {
    // Récupérer les données du formulaire
    const orders = [];
    const rows = document.querySelectorAll('#orders-body tr');
    
    rows.forEach(row => {
        const drugName = row.querySelector('.orders-name').textContent;
        const quantity = parseInt(row.querySelector('.orders-quantity').textContent);
        orders.push({ drugName, quantity });
    });

    const formData = {
        //userId: parseInt(document.getElementById('userId').textContent),
        dateOrders: new Date().toISOString().split('T')[0], // Date du jour
        items: orders
    };

    try {
        const response = await fetch('/orders/save', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });

        const result = await response.json();
        if (response.ok) {
            // Ouvrir le PDF dans un nouvel onglet
            window.open(result.pdfUrl, '_blank');
        } else {
            alert('Erreur: ' + result.message);
        }
    } catch (error) {
        console.error('Erreur:', error);
        alert('Une erreur est survenue');
    }
});**/
 


/***function submitOrdersForm() {
    const orders = [];
    const rows = document.querySelectorAll("#orders-body tr");
    
    rows.forEach(row => {
        const name = row.querySelector(".orders-name").textContent;
        const quantity = parseInt(row.querySelector(".orders-quantity").textContent);
        orders.push({ drugName: name, quantity: quantity });
    });

    const formData = {
        dateOrders: new Date().toISOString().split('T')[0],
        items: orders,
        userId: document.getElementById("userId").textContent
    };

    fetch('/orders/save', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData)
    })
    .then(response => response.json())
    .then(data => {
        if(data.pdfUrl) {
            window.open(data.pdfUrl, '_blank');
        }
    })
    .catch(error => console.error('Error:', error));
}***/

//FORMATAGE JSON POUR LES HISTORIQUES
/**document.querySelectorAll('pre').forEach(e1 => {
    try {
        const json = JSON.parse(e1.textContent);
        e1.textContent = JSON.stringify(json, null, 2);
    } catch(e) {}
});

// Formattage du JSON dans la page
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('pre.json').forEach(el => {
        try {
            const json = JSON.parse(el.textContent);
            el.textContent = JSON.stringify(json, null, 2);
        } catch(e) {
            console.error('Erreur de parsing JSON', e);
        }
    });
});**/

// Formattage du JSON dans la page
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

//MODEL POUR HISTORY PAGE

document.addEventListener('DOMContentLoaded', function() {
    const modal = new bootstrap.Modal(document.getElementById('detailsModal'));
    const beforeValues = document.getElementById('beforeValues');
    const afterValues = document.getElementById('afterValues');
    
    document.querySelectorAll('.details-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const before = this.getAttribute('data-before');
            const after = this.getAttribute('data-after');
            
            // Formater le JSON pour un affichage plus lisible
            beforeValues.textContent = before ? JSON.stringify(JSON.parse(before), null, 2) : "Aucune donnée";
            afterValues.textContent = after ? JSON.stringify(JSON.parse(after), null, 2) : "Aucune donnée";
            
            modal.show();
        });
    });
});
/**** SALE PDF JS ****/
//Mettre et retirer du div pour les ventes effectuées

function addToSaleDiv(clickedElement) {
    const row = clickedElement.closest('tr');
    const saleId = row.dataset.saleId;
    const targetBody = document.getElementById('sale-body');

    // Vérifier si déjà présent
    if (targetBody.querySelector(`[data-sale-id="${saleId}"]`)) {
        alert("Ligne déjà ajoutée !");
        return;
    }

    // Cloner les données (Nom, Quantité, PU, PT)
    const newRow = document.createElement('tr');
    newRow.dataset.saleId = saleId;
    newRow.innerHTML = `
        <td>${row.cells[0].textContent}</td>
        <td>${row.cells[1].textContent}</td>
        <td>${row.cells[2].textContent}</td>
        <td>${row.cells[3].textContent}</td>
        <td><span onclick="this.closest('tr').remove()"><img src="/imgs/remove_circle_outline.svg"></span></td>
    `;

    targetBody.appendChild(newRow);
}

function removeFromSaleDiv(clickedElement) {
    const row = clickedElement.closest('tr');
    // Si la ligne est dans le div, supprimer directement
    if (row.closest('#sale-body')) {
        row.remove();
        return;
    }
    // Si la ligne est dans le tableau principal, chercher dans le div
    const saleId = row.dataset.saleId;
    const targetRow = document.querySelector(`#sale-body [data-sale-id="${saleId}"]`);

    if (targetRow) {
        targetRow.remove();
    } else {
        alert("Ligne pas encore ajoutée !");
    }
}
/***  POUR AFFICHER LE MOT DE PASSE ******/
let eyeicon = document.getElementById("eyeicon");
let password = document.getElementById("password");

eyeicon.onclick = function(){
    if(password.type == "password"){
        password.type = "text";
    }else{
        password.type = "password";
    }
}
 /****CODE POUR FAIRE DISPARAITRE LE PETIT DIV D'ALERTE***/
 // Exécuter seulement si la div existe
/**window.addEventListener('DOMContentLoaded', () => {
    const alertDiv = document.getElementById('autoHideAlert');

    if(alertDiv) {
        //cacher après 7 sec
        setTimeout(() => {
            alertDiv.style.opacity = '0';
            // Optionnel: Supprimer complètement après la transition
            setTimeout(()=>{
                alertDiv.remove();
            },300);
        },7000);
    }
 });**/
 window.addEventListener('DOMContentLoaded', () => {
    const alertDiv = document.getElementById('autoHideAlert');
    alertDiv && setTimeout(() => alertDiv.remove(), 7000);
});




