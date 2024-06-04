document.getElementById('transaction-form').addEventListener('submit', function(e) {
    e.preventDefault();

    const description = document.getElementById('description').value;
    const amount = document.getElementById('amount').value;

    fetch('/transacao', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({description, amount})
    })
    .then(response => response.json())
    .then(data => {
        alert(data.status);
        loadTransactions();
        loadBlockchain();
    })
    .catch(error => {
        console.error('Error:', error);
    });
});

function loadTransactions() {
    fetch('/transacoes', {cache: 'no-cache'})
    .then(response => response.json())
    .then(data => {
        const transactionsDiv = document.getElementById('transactions');
        transactionsDiv.innerHTML = '';
        data.forEach(transaction => {
            const transactionDiv = document.createElement('div');
            transactionDiv.textContent = `${transaction.description}: $${transaction.amount}`;
            transactionsDiv.appendChild(transactionDiv);
        });
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

document.getElementById('register-transactions').addEventListener('click', function() {
    const button = document.getElementById('register-transactions');
    button.classList.add('loading');
    button.disabled = true;

    fetch('/registrar-transacoes', {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        alert(data.status);
        loadBlockchain();
        loadTransactions(); 
    })
    .finally(() => {
        button.classList.remove('loading');
        button.disabled = false;
    })
    .catch(error => {
        console.error('Error:', error);
        button.classList.remove('loading');
        button.disabled = false;
    });
});

function loadBlockchain() {
    fetch('/cadeia', {cache: 'no-cache'})
    .then(response => response.json())
    .then(data => {
        const blockchainContainer = document.getElementById('blockchain-container');
        blockchainContainer.innerHTML = '';
        data.forEach(block => {
            const blockDiv = document.createElement('div');
            blockDiv.className = 'block';
            blockDiv.innerHTML = `
                <div class="block-content">
                    <label>Bloco:</label>
                    <input type="text" value="${block.index}" readonly>
                    <label>Nonce:</label>
                    <input type="text" value="${block.nonce}" readonly>
                    <label>Dados:</label>
                    <textarea readonly>${JSON.stringify(block.transactions, null, 2)}</textarea>
                    <label>Prévio:</label>
                    <input type="text" value="${block['previous-hash']}" readonly>
                    <label>Hash:</label>
                    <input type="text" value="${block.hash}" readonly>
                </div>
            `;
            blockchainContainer.appendChild(blockDiv);
        });
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

document.addEventListener('DOMContentLoaded', function() {
    loadTransactions();
    loadBlockchain();
});
