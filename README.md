# NutriApp

NutriApp es una aplicación diseñada para ayudar a los usuarios a gestionar sus entrenamientos y dietas. Permite a los usuarios interactuar con entrenadores, recibir planes de entrenamiento y dieta personalizados, así como realizar un seguimiento de su progreso. Los entrenadores pueden gestionar solicitudes, ver el progreso de los usuarios y proporcionar retroalimentación en tiempo real.

## Características

- **Autenticación de usuarios**: Los usuarios pueden registrarse y autenticar su cuenta.
- **Mensajería**: Los usuarios y entrenadores pueden comunicarse en tiempo real a través de un sistema de mensajería.
- **Notificaciones**: Los usuarios reciben notificaciones sobre sus solicitudes, entrenamientos y mensajes.
- **Solicitudes**: Los usuarios pueden enviar solicitudes de entrenamiento o dietas, y los entrenadores pueden gestionarlas.
- **Perfiles de usuarios y entrenadores**: Los usuarios y entrenadores pueden ver y actualizar su información personal.
- **Reseñas y valoraciones**: Los usuarios pueden dejar reseñas sobre los entrenadores.
- **Gestión de dietas**: Los usuarios pueden ver sus planes de dieta proporcionados por los entrenadores.

## Tecnologías utilizadas

- **Firebase**: Para autenticación, almacenamiento de datos en tiempo real y gestión de la base de datos.
- **Kotlin**: Lenguaje de programación utilizado para el desarrollo de la aplicación.
- **Android SDK**: Herramientas para el desarrollo de la aplicación Android.
- **RecyclerView**: Para mostrar listas de elementos (mensajes, notificaciones, solicitudes, etc.) en la interfaz.
- **Firebase Cloud Messaging (FCM)**: Para el envío de notificaciones push.

## Estructura del Proyecto

El proyecto está organizado en varias clases que representan las funcionalidades principales de la aplicación. A continuación se muestra la descripción de las clases principales:

### Clases Principales:

- **Message**: Representa un mensaje entre un usuario y un entrenador.
- **MessagesActivity**: Actividad que maneja la visualización y envío de mensajes.
- **Notification**: Representa una notificación enviada a los usuarios.
- **NotificationsActivity**: Actividad que muestra las notificaciones recibidas por el usuario.
- **Profile**: Contiene los datos del perfil del usuario o entrenador.
- **ProfileActivity**: Actividad que permite al usuario o entrenador ver y editar su perfil.
- **Request**: Representa una solicitud realizada por el usuario o entrenador (ej. solicitud de entrenamiento).
- **RequestAdapter**: Adaptador que muestra las solicitudes en un RecyclerView.
- **Review**: Representa una reseña dejada por un usuario sobre un entrenador.
- **ReviewsActivity**: Actividad que muestra las reseñas de un entrenador.
- **SignUpActivity**: Actividad que permite a los usuarios registrarse en la aplicación.
- **Trainer**: Contiene los detalles de un entrenador.
- **TrainerAdapter**: Adaptador que muestra una lista de entrenadores.
- **TrainerListActivity**: Actividad que muestra la lista de entrenadores disponibles.
- **TrainerRequestsActivity**: Actividad donde los entrenadores pueden ver y gestionar las solicitudes de los usuarios.
- **UpdateWeightActivity**: Actividad que permite a los usuarios actualizar su peso.
- **User**: Contiene los detalles del perfil de un usuario.
- **UserAdapter**: Adaptador que muestra una lista de usuarios.
- **UserBaseActivity**: Actividad base donde el usuario puede gestionar su progreso.
- **ViewDietActivity**: Actividad que muestra el plan de dieta de un usuario.
- **ViewUsersActivity**: Actividad donde los entrenadores pueden ver la lista de usuarios.

## Instalación

Para comenzar a trabajar con el proyecto, sigue los siguientes pasos:

### Prerequisitos

Asegúrate de tener instalado:

- [Android Studio](https://developer.android.com/studio)
- [JDK 8 o superior](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
- Una cuenta de [Firebase](https://firebase.google.com/)
