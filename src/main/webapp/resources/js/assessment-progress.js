document.addEventListener('DOMContentLoaded', function () {
    const progressBar = document.getElementById('progress-bar');
    const progressCount = document.getElementById('progress-count');

    // Fallback to 1 to avoid division by zero errors
    const totalQuestions = Number(document.body.dataset.totalQuestions) || 1;
    const allRadios = document.querySelectorAll('input[type="radio"]');

    function updateProgress() {
        const answeredNames = new Set();

        // Only count checked radios
        document.querySelectorAll('input[type="radio"]:checked').forEach(radio => {
            answeredNames.add(radio.name);
        });

        const count = answeredNames.size;
        const percent = Math.min((count / totalQuestions) * 100, 100);

        // Update UI
        if (progressCount) progressCount.textContent = count;
        if (progressBar) progressBar.style.width = percent + '%';
    }

    // 1. Attach listeners ONCE outside the update function
    allRadios.forEach(radio => {
        radio.addEventListener('change', updateProgress);
    });

    // 2. Run once on load to catch pre-filled browser values
    updateProgress();
});
