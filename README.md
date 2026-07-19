\# Sistema de Gestión de Guías de Despacho Cloud Native



Proyecto desarrollado para la Evaluación Final Transversal - Semana 9 de la asignatura Desarrollo Cloud Native.



La solución implementa un sistema de gestión de guías de despacho utilizando microservicios Spring Boot, autenticación mediante Azure AD B2C, exposición de endpoints con Amazon API Gateway, mensajería asíncrona con RabbitMQ, almacenamiento de documentos en Amazon S3 y despliegue automatizado mediante GitHub Actions sobre una instancia EC2 con Docker Compose.



\---



\## Objetivo del proyecto



Desarrollar una solución cloud native que permita crear, consultar, actualizar, eliminar, procesar y almacenar guías de despacho, integrando componentes cloud y patrones de comunicación asíncrona.



El sistema permite:



\- Crear guías de despacho desde una API REST.

\- Publicar mensajes en RabbitMQ desde el microservicio productor.

\- Consumir mensajes desde un microservicio consumidor.

\- Procesar la guía y generar un documento PDF.

\- Subir el documento generado a Amazon S3.

\- Registrar la ruta S3 del archivo procesado.

\- Manejar errores mediante una Dead Letter Queue.

\- Proteger los endpoints mediante JWT emitidos por Azure AD B2C.

\- Exponer los servicios mediante Amazon API Gateway.

\- Automatizar el despliegue con GitHub Actions.



\---



\## Arquitectura implementada



La solución se compone de los siguientes elementos:



| Componente | Tecnología | Descripción |

|---|---|---|

| Microservicio productor | Spring Boot | Genera guías de despacho y publica mensajes en RabbitMQ |

| Microservicio consumidor | Spring Boot | Consume mensajes, procesa guías y sube documentos a S3 |

| Mensajería | RabbitMQ | Cola principal, exchange, routing key y DLQ |

| Seguridad | Spring Security + Azure AD B2C | Validación de JWT, audience, issuer, policy y scope |

| API Manager | Amazon API Gateway | Exposición de endpoints públicos protegidos con JWT |

| Almacenamiento cloud | Amazon S3 | Almacenamiento final de documentos PDF |

| Despliegue | EC2 + Docker Compose | Ejecución de productor, consumidor y RabbitMQ |

| CI/CD | GitHub Actions | Compilación, validación y despliegue automatizado |



\---



\## Microservicios



\### Productor de guías



Ubicación:



```text

src/main/java/cl/duoc/gestionguias

