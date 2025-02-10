function formatMessages() {
    const chatItems = document.querySelectorAll('.chat-history-item');

    chatItems.forEach(item => {
        const markdownText = item.querySelector('p').innerText;
        const htmlContent = marked.parse(markdownText);
        const container = item.querySelector('div.marked-container')
        container.innerHTML = htmlContent; 
    });
}

function sendOnEnter() {
    // Get references to the textarea and button
    const textarea = document.getElementById('text');
    const button = document.getElementById('sendButton');

    // Add an event listener to the textarea
    textarea.addEventListener('keydown', function(event) {
        // Check if the Enter key is pressed
        if (event.key === 'Enter') {
            event.preventDefault(); // Prevent the default action (new line)
            button.click(); // Trigger the button click
        }
    });
}

// Initialize on document load
document.addEventListener("DOMContentLoaded", () => {
    sendOnEnter();
    formatMessages();
});
