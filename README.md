Daterra 🌱

Daterra es una aplicación móvil orientada a la sustentabilidad y protección del medio ambiente en Chile. Su objetivo principal es facilitar a los ciudadanos el acceso a la información sobre reciclaje, ubicando Puntos Limpios cercanos mediante geolocalización y educando sobre la correcta separación de residuos.

✨ Funcionalidades Principales

🗺️ Mapa Interactivo de Puntos Limpios: Integración con Google Maps para visualizar los centros de reciclaje. Utiliza la ubicación GPS del usuario para encontrar el punto más cercano e indicar cómo llegar.

🔐 Autenticación Segura (JWT): Sistema de Registro e Inicio de Sesión conectado a una API REST (Render).

💾 Sesiones Persistentes: Uso de Preferences DataStore para mantener la sesión iniciada (Auto-Login) y guardar la información básica del perfil de manera local y segura.

📚 Sección Educativa ("Infórmate"): Blog integrado con artículos, guías de reciclaje y calculadoras de huella de carbono para educar al ciudadano.

👤 Gestión de Perfil: Visualización de datos del usuario, opciones de edición y cierre de sesión seguro (limpieza de tokens).

🛠️ Tecnologías y Arquitectura

El proyecto está desarrollado de forma nativa para Android utilizando las herramientas y librerías más modernas recomendadas por Google:

Lenguaje: Kotlin

UI Toolkit: Jetpack Compose (100% UI Declarativa)

Arquitectura: MVVM (Model-View-ViewModel)

Navegación: Jetpack Navigation Compose

Conexión a Red: Retrofit2

Almacenamiento Local: Preferences DataStore (Reemplazo moderno de SharedPreferences)

Mapas y Ubicación: Google Maps SDK for Android + Fused Location Provider

Asincronismo: Coroutines & Kotlin Flow

🚀 Instalación y Configuración

Si deseas clonar y probar este proyecto en tu entorno local:

Clona el repositorio:

git clone [https://github.com/TU_USUARIO/daterra.git](https://github.com/TU_USUARIO/daterra.git)


Abre el proyecto en Android Studio.

Configuración de Google Maps API: * Necesitarás una clave API de Google Maps.

Crea un archivo local.properties en la raíz del proyecto (si no existe) y agrega tu clave:

MAPS_API_KEY=tu_clave_api_aqui


Sincroniza el proyecto con Gradle (Sync Project with Gradle Files).

Ejecuta la aplicación en un emulador o dispositivo físico con Android.

🌐 Conexión al Backend

La aplicación consume dos orígenes de datos principales:

API de Autenticación (Render): Maneja el registro y login de usuarios ciudadanos.

API MMA (Gobierno de Chile): (Implementación sujeta al flujo del proyecto) Para la obtención de puntos limpios a nivel nacional.



👥 Desarrolladores

Giuseppe Saavedra - Desarrollo Frontend Android (Jetpack Compose)
José Carrasco - Backend
Matías Sepúlveda - Desarrollo Frontend Web

[Nombre de tu Compañero] - Desarrollo Backend / Base de Datos

Datos claros para una tierra limpia. 🌎
