
function showApiError(message) {
  const toastEl = document.getElementById('errorToast');
  const toastBody = document.getElementById('errorToastBody');
  if (!toastEl || !toastBody) return;

  toastBody.textContent = message || 'An unexpected error occurred.';

  const toast = new bootstrap.Toast(toastEl, { delay: 5000 });
  toast.show();
}


window.addEventListener('unhandledrejection', function(event) {
  console.error('Unhandled promise rejection:', event.reason);
  showApiError('An unexpected error occurred. Please refresh and try again.');
  event.preventDefault();
});


window.addEventListener('error', function(event) {
  if (event.target && (event.target.src || event.target.href)) return;
  console.error('Global JS error:', event.message);
  showApiError('A client-side error occurred. The team has been notified.');
});
