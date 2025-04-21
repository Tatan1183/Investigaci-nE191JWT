// Espera a que el contenido del DOM esté completamente cargado
document.addEventListener('DOMContentLoaded', () => {

    // --- Selección de Elementos del DOM ---
    const loginForm = document.getElementById('loginForm');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const loginMessage = document.getElementById('loginMessage');

    const getDataBtn = document.getElementById('getDataBtn');
    const protectedData = document.getElementById('protectedData');
    const errorMessage = document.getElementById('errorMessage');

    // --- URLs del Backend ---
    // Asegúrate que el puerto sea el correcto (8086 en tu caso)
    const API_BASE_URL = 'http://localhost:8086/api';
    const AUTH_URL = `${API_BASE_URL}/auth/authenticate`;
    const PROTECTED_URL = `${API_BASE_URL}/greeting/sayHelloProtected`;

    // --- Función para mostrar mensajes ---
    function showMessage(element, message, isError = false) {
        element.textContent = message;
        element.className = `message ${isError ? 'error' : 'success'}`;
        element.style.display = 'block';
        // Ocultar mensaje después de unos segundos
        setTimeout(() => {
            element.style.display = 'none';
        }, 5000);
    }

    // --- Manejador del Formulario de Login ---
    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault(); // Previene el envío tradicional del formulario
        loginMessage.style.display = 'none'; // Oculta mensajes anteriores

        const email = emailInput.value;
        const password = passwordInput.value;

        console.log(`Intentando login para: ${email}`); // Log para depuración

        try {
            const response = await fetch(AUTH_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json' // Indicamos que enviamos JSON
                },
                body: JSON.stringify({email: email, password: password}) // Convertimos el objeto a JSON
            });

            console.log(`Respuesta del login: ${response.status} ${response.statusText}`); // Log

            if (response.ok) { // Si el status es 2xx (ej. 200 OK)
                const data = await response.json(); // Extraemos el cuerpo JSON
                const token = data.token;

                if (token) {
                    console.log('Token recibido:', token);
                    // --- Almacenamiento del Token ---
                    // localStorage persiste incluso después de cerrar el navegador
                    // sessionStorage se borra al cerrar la pestaña/navegador
                    localStorage.setItem('jwtToken', token);
                    showMessage(loginMessage, '¡Login exitoso! Token guardado.');
                } else {
                    showMessage(loginMessage, 'Login exitoso pero no se recibió token.', true);
                }
                loginForm.reset(); // Limpia el formulario

            } else {
                // Manejo de errores de login (ej. 401 Unauthorized, 403 Forbidden si las credenciales son incorrectas)
                const errorText = await response.text(); // Intenta leer el cuerpo como texto
                console.error('Error en login:', response.status, response.statusText, errorText);
                showMessage(loginMessage, `Error en login: ${response.status} ${response.statusText}. ${errorText || 'Credenciales incorrectas o error del servidor.'}`, true);
            }

        } catch (error) {
            // Errores de red u otros problemas con fetch
            console.error('Error de red o fetch:', error);
            showMessage(loginMessage, `Error de conexión: ${error.message}`, true);
        }
    });

    // --- Manejador del Botón para Obtener Datos Protegidos ---
    getDataBtn.addEventListener('click', async () => {
        errorMessage.style.display = 'none'; // Oculta errores anteriores
        protectedData.textContent = '--- Cargando... ---';

        // --- Recuperar el Token Almacenado ---
        const token = localStorage.getItem('jwtToken');

        if (!token) {
            showMessage(errorMessage, 'Error: No se encontró token. Por favor, inicia sesión.', true);
            protectedData.textContent = '--- Necesitas iniciar sesión ---';
            return;
        }

        console.log('Intentando acceder a recurso protegido con token:', token); // Log

        try {
            const response = await fetch(PROTECTED_URL, {
                method: 'GET',
                headers: {
                    // --- Cabecera de Autorización ---
                    // Esencial para enviar el token al backend
                    'Authorization': `Bearer ${token}`
                }
            });

            console.log(`Respuesta del recurso protegido: ${response.status} ${response.statusText}`); // Log

            if (response.ok) { // Si el status es 200 OK
                const data = await response.text(); // El endpoint devuelve texto plano
                protectedData.textContent = data;
                errorMessage.style.display = 'none'; // Oculta el mensaje de error si había uno
            } else {
                // Manejo de errores (401 Unauthorized si el token es inválido/expirado,
                // 403 Forbidden si el token es válido pero no tienes permisos)
                const errorText = await response.text(); // Intenta leer el cuerpo como texto
                console.error('Error al obtener datos protegidos:', response.status, response.statusText, errorText);
                showMessage(errorMessage, `Error ${response.status}: No se pudo acceder al recurso. ${errorText || 'Verifica el token o los permisos.'}`, true);
                protectedData.textContent = '--- Error al cargar datos ---';

                // **NOTA IMPORTANTE PARA TI:** Si obtienes 403 aquí, es muy probable
                // que sea por el problema de autorización que aún tienes en el backend,
                // incluso si el token *es* válido.
            }

        } catch (error) {
            // Errores de red u otros problemas con fetch
            console.error('Error de red o fetch:', error);
            showMessage(errorMessage, `Error de conexión: ${error.message}`, true);
            protectedData.textContent = '--- Error de conexión ---';
        }
    });

}); // Fin de DOMContentLoaded