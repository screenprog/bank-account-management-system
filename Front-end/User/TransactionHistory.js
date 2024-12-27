const imageOutput = document.querySelector('.profile .avatars .image');
imageOutput.src = `data:image/png;base64,${localStorage.getItem("image")}`;
document.querySelector('.profile p').textContent = localStorage.getItem("name");
document.addEventListener("DOMContentLoaded", function() {
    const account = localStorage.getItem("accountId");
    const apiUrl = `http://localhost:8080/user/transaction-history?id=${account}`; 
    const transactionsTableBody = document.querySelector('.transactions tbody');

    // Function to fetch transaction data from the API
    async function fetchTransactions() {
        const token = localStorage.getItem('token'); 

        try {
            const response = await fetch(apiUrl, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
     
            const transactions = await response.json();
            populateTable(transactions);
        } catch (error) {
            console.error('There has been a problem with your fetch operation:', error);
        }
    }

    // Function to populate the table with transaction data
    function populateTable(transactions) {
        transactionsTableBody.innerHTML = ''; // Clear existing table data
        transactions.reverse()
        transactions.forEach(transaction => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${transaction.description}</td>
                <td>Rs. ${transaction.amount}</td>
                <td>${transaction.transactionDate}</td>
                <td>₹${transaction.balanceLeft}</td>
            `;
            transactionsTableBody.appendChild(row);
        });
    }

    // Fetch transactions when the page loads
    fetchTransactions();
});