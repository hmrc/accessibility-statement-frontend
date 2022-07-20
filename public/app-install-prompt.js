let deferredPrompt;

window.addEventListener('load', (e) => {
    alert('page load called')

    window.addEventListener('beforeinstallprompt', (e) => {
        // Prevent Chrome 67 and earlier from automatically showing the prompt
        e.preventDefault();
        // Stash the event so it can be triggered later.
        deferredPrompt = e;
        // Update UI notify the user they can add to home screen
        alert("beforeinstallprompt fired!")
    });
});
