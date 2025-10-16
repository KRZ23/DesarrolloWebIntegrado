# 🎨 Generación de Diagrama ER (Entity-Relationship)

## 📋 Tabla de Contenidos
- [Opciones Disponibles](#-opciones-disponibles)
- [Opción 1: Script Automático (Recomendado)](#-opción-1-script-automático-recomendado)
- [Opción 2: Maven Plugin](#-opción-2-maven-plugin)
- [Opción 3: IntelliJ IDEA](#-opción-3-intellij-idea)
- [Opción 4: DBeaver](#-opción-4-dbeaver)

---

## 🎯 Opciones Disponibles

### ⭐ Opción 1: Script Automático con SchemaSpy (Recomendado)

**Ventajas:**
- ✅ Generación automática de diagramas interactivos en HTML
- ✅ Muestra relaciones, índices, constraints
- ✅ Incluye estadísticas y metadatos
- ✅ Navegación visual entre tablas

**Requisitos:**
- Java 11+
- Conexión a internet (primera vez para descargar dependencias)

**Uso:**

```bash
# 1. Ejecutar el script
./generate-er-diagram.sh

# 2. Abrir el resultado
# El navegador se abrirá automáticamente o puedes abrir manualmente:
# ./database-docs/index.html
```

**Resultado:**
- Carpeta `database-docs/` con sitio web completo
- `index.html` → Página principal con todas las tablas
- `relationships.html` → Diagrama de relaciones
- `orphans.html` → Tablas sin relaciones
- Archivos SVG con diagramas de cada tabla

---

### 🔧 Opción 2: Maven Plugin

**Ventajas:**
- ✅ Integrado con Maven
- ✅ No requiere script separado

**Uso:**

```bash
# 1. Compilar el proyecto primero
./mvnw clean package -DskipTests

# 2. Iniciar la aplicación para crear la BD H2
./mvnw spring-boot:run &
sleep 10  # Esperar que se cree la BD

# 3. Generar el diagrama
./mvnw exec:java@generate-er-diagram

# 4. Detener la aplicación
pkill -f spring-boot:run

# 5. Abrir el resultado
xdg-open target/database-docs/index.html
```

**Resultado:**
- Carpeta `target/database-docs/` con el diagrama

---

### 🖥️ Opción 3: IntelliJ IDEA (Si usas este IDE)

**Ventajas:**
- ✅ Sin configuración adicional
- ✅ Vista en vivo de la BD
- ✅ Actualización automática

**Pasos:**

1. **Conectar a la BD H2:**
   - View → Tool Windows → Database (o Alt+1)
   - Click en `+` → Data Source → H2
   - **URL:** `jdbc:h2:file:./data/impuestoscal`
   - **User:** `sa`
   - **Password:** (vacío)
   - Click "Test Connection" → "OK"

2. **Generar Diagrama:**
   - Click derecho en `PUBLIC` schema
   - Diagrams → Show Diagram → Choose layout
   - Opciones:
     - `Database Diagram` → Diagrama ER completo
     - `Show Visualization` → Vista interactiva

3. **Exportar:**
   - Click derecho en el diagrama
   - Export → To Image (PNG/SVG)
   - Save as → Elegir ubicación

**Resultado:**
- Imagen PNG o SVG del diagrama
- Vista interactiva en el IDE

---

### 🐘 Opción 4: DBeaver (Herramienta Gratuita)

**Ventajas:**
- ✅ Multiplataforma
- ✅ Gratuito y open source
- ✅ Muy visual

**Pasos:**

1. **Instalar DBeaver:**
   ```bash
   # Ubuntu/Debian
   sudo snap install dbeaver-ce
   
   # O descargar desde: https://dbeaver.io/download/
   ```

2. **Conectar a H2:**
   - Nueva Conexión → H2
   - **Path:** `/ruta/al/proyecto/data/impuestoscal`
   - **JDBC URL:** `jdbc:h2:file:./data/impuestoscal`
   - **User:** `sa`
   - **Password:** (vacío)
   - Test Connection → Finish

3. **Generar Diagrama:**
   - Expandir conexión → PUBLIC → Tables
   - Seleccionar todas las tablas (Ctrl+A)
   - Click derecho → View Diagram
   - Layout → Horizontal/Vertical/Circular

4. **Exportar:**
   - Click en el ícono de exportación
   - Formato: PNG, SVG, PDF
   - Save

**Resultado:**
- Imagen del diagrama en formato elegido

---

## 📊 Estructura de la Base de Datos

### Entidades Principales

```
Usuario
├── RegistroTributario (1:N)
├── ReciboHonorario (1:N)
├── OperacionIGV (1:N)
└── Trabajador (1:N)

Role (relación N:M con Usuario)
```

### Detalles de Relaciones

| Tabla | Relación | Tabla Relacionada | Tipo |
|-------|----------|-------------------|------|
| `usuario` | `role_id` | `role` | ManyToOne |
| `registro_tributario` | `usuario_id` | `usuario` | ManyToOne |
| `recibo_honorario` | `usuario_id` | `usuario` | ManyToOne |
| `operacion_igv` | `usuario_id` | `usuario` | ManyToOne |
| `trabajador` | `usuario_id` | `usuario` | ManyToOne |

### Campos Comunes (Auditoría)

Todas las entidades incluyen:
- `fecha_creacion` (LocalDateTime)
- `fecha_actualizacion` (LocalDateTime)
- `activo` (Boolean) - Para eliminación lógica

---

## 🔍 Ejemplo de Diagrama

```
┌─────────────────┐
│    USUARIO      │
├─────────────────┤
│ id (PK)         │──┐
│ username        │  │
│ password        │  │
│ role_id (FK)    │──┼───┐
└─────────────────┘  │   │
                      │   │
        ┌─────────────┼───┘
        │             │
        ▼             ▼
┌─────────────────┐ ┌──────────────────┐
│ RECIBO_HONOR    │ │      ROLE        │
├─────────────────┤ ├──────────────────┤
│ id (PK)         │ │ id (PK)          │
│ usuario_id (FK) │ │ nombre           │
│ numero_recibo   │ └──────────────────┘
│ monto_total     │
│ retencion (8%)  │
│ monto_neto      │
│ activo          │
└─────────────────┘
        │
        │ (Similar para OPERACION_IGV y TRABAJADOR)
        ▼
```

---

## 🚀 Recomendación Final

**Para presentaciones y documentación:**
- Usa la **Opción 1 (Script)** para tener un sitio web completo e interactivo
- Exporta capturas de `relationships.html` para incluir en presentaciones

**Para desarrollo diario:**
- Usa **IntelliJ IDEA** (Opción 3) si ya lo tienes
- O **DBeaver** (Opción 4) por su facilidad de uso

**Para integración en CI/CD:**
- Usa el **Maven Plugin** (Opción 2) en tu pipeline

---

## 📝 Notas

- La BD H2 debe existir antes de generar el diagrama
- Ejecuta la aplicación al menos una vez para crear las tablas
- El diagrama se regenera automáticamente al ejecutar el script
- Los archivos HTML generados son estáticos y portables

---

## 🐛 Solución de Problemas

### Error: "Database not found"
```bash
# Solución: Ejecutar la app primero
./mvnw spring-boot:run
# Esperar que inicie y luego Ctrl+C
```

### Error: "wget: command not found"
```bash
# Ubuntu/Debian
sudo apt-get install wget

# O descargar manualmente desde:
# https://github.com/schemaspy/schemaspy/releases
```

### El diagrama no muestra relaciones
- Verifica que las anotaciones JPA están correctas (@ManyToOne, @OneToMany)
- Asegúrate que las FK están definidas con @JoinColumn

---

## 📚 Referencias

- [SchemaSpy Documentation](https://schemaspy.readthedocs.io/)
- [H2 Database](https://www.h2database.com/)
- [DBeaver](https://dbeaver.io/)
