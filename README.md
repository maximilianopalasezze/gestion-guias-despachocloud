# Sistema de Gestion de Guias de Despacho Cloud Native

Proyecto Spring Boot para la semana 3: **Construyendo y desplegando una solucion Cloud Native**.

## Objetivo

Desarrollar un microservicio para crear, actualizar, descargar, eliminar y consultar guias de despacho, usando:

- Spring Boot
- DTOs en controllers
- EFS como almacenamiento temporal
- AWS S3 como almacenamiento final
- Docker
- Docker Hub
- EC2
- GitHub Actions para CI/CD

## Importante sobre la retroalimentacion del profesor

Este proyecto evita exponer entidades en los controllers.  
Los controllers reciben y devuelven DTOs.  
La entidad `Guia` solo se usa en las capas `Service` y `Repository`.

## Endpoints principales

### Crear guia

POST `/api/guias`

```json
{
  "transportista": "Transportista X",
  "destinatario": "Cliente Prueba",
  "direccionDestino": "Av. Siempre Viva 123",
  "descripcionPedido": "Pedido de productos tecnologicos",
  "pesoKg": 12.5,
  "fechaDespacho": "2026-06-07"
}
```

### Subir guia a S3

POST `/api/guias/{id}/subir-s3`

### Descargar guia con validacion de permisos

GET `/api/guias/{id}/descargar?transportista=Transportista X`

Si el transportista no coincide con el de la guia, responde 403.

### Actualizar guia

PUT `/api/guias/{id}`

```json
{
  "transportista": "Transportista X",
  "destinatario": "Cliente Actualizado",
  "direccionDestino": "Nueva direccion 456",
  "descripcionPedido": "Pedido actualizado",
  "pesoKg": 15.75,
  "fechaDespacho": "2026-06-08"
}
```

### Eliminar guia

DELETE `/api/guias/{id}`

### Consultar historial

GET `/api/guias`

GET `/api/guias?transportista=Transportista X`

GET `/api/guias?fecha=2026-06-07`

GET `/api/guias?transportista=Transportista X&fecha=2026-06-07`

## Prueba local en NetBeans

1. Abrir NetBeans.
2. File > Open Project.
3. Seleccionar esta carpeta.
4. Ejecutar `GestionGuiasDespachoCloudApplication.java`.
5. Probar en Postman con:

```txt
http://localhost:8080/api/guias
```

En local, S3 queda desactivado por defecto:

```properties
aws.s3.enabled=false
```

## Prueba real en EC2

En la evidencia final se debe usar la IP publica de EC2, por ejemplo:

```txt
http://IP_PUBLICA_EC2:8080/api/guias
```

No usar localhost en el video final.

## Variables requeridas en EC2 / GitHub Actions

- `AWS_REGION`
- `AWS_S3_BUCKET_NAME`
- `AWS_S3_ENABLED=true`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `EFS_BASE_PATH=/mnt/efs/guias`

## Secrets requeridos en GitHub

- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`
- `EC2_HOST`
- `EC2_USER`
- `EC2_SSH_KEY`
- `AWS_REGION`
- `AWS_S3_BUCKET_NAME`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`

## Comando Docker manual en EC2

```bash
docker run -d \
  --name gestion-guias \
  --restart=always \
  -p 8080:8080 \
  -e AWS_REGION=us-east-1 \
  -e AWS_S3_BUCKET_NAME=TU_BUCKET \
  -e AWS_S3_ENABLED=true \
  -e AWS_ACCESS_KEY_ID=TU_ACCESS_KEY \
  -e AWS_SECRET_ACCESS_KEY=TU_SECRET_KEY \
  -e EFS_BASE_PATH=/mnt/efs/guias \
  -v /mnt/efs/guias:/mnt/efs/guias \
  TU_USUARIO_DOCKER/gestion-guias-despacho-cloud:latest
```

## Evidencia recomendada para el video

1. Mostrar codigo y explicar que los controllers usan DTOs.
2. Mostrar EFS montado en EC2.
3. Mostrar bucket S3.
4. Mostrar GitHub Actions exitoso.
5. Mostrar Docker Hub con la imagen.
6. Mostrar `docker ps` en EC2.
7. Probar endpoints en Postman usando IP publica EC2.
8. Crear guia.
9. Mostrar archivo creado en EFS.
10. Mostrar archivo subido a S3.
11. Descargar guia.
12. Actualizar guia.
13. Consultar historial por fecha y transportista.
14. Eliminar guia.
