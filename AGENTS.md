# AGENTS.md

This file describes the agent configuration for this project.

## Overview

This is a Maven-based Java project (FROST-Client-Dynamic) that implements a client library for the OGC SensorThings API with dynamic data model support.

## Project Structure

- **Main source**: `src/main/java/`
- **Test source**: `src/test/java/`
- **Build file**: `pom.xml`
- **Code style**: Eclipse formatter configuration in `scripts/codestyle/formatter.xml`
- **License header**: `scripts/license-header`

## Key Commands

Use the Maven wrapper (`./mvnw`) instead of system `mvn` for consistent builds.

- **Build**: `./mvnw clean install`
- **Build (quiet)**: `./mvnw -q clean install`
- **Format code**: `./mvnw spotless:apply`
- **Run tests**: `./mvnw test`
- **Run tests (quiet)**: `./mvnw -q test`
- **Generate docs**: `./mvnw javadoc:javadoc`

## Code Style

- Java 17
- Maven project with standard directory layout (use `./mvnw` for builds)
- Eclipse formatting (version 4.21)
- License headers required on all Java files
- Use imports, not full package names when using a Class/Interface
- Use `spotless:apply` before committing to ensure formatting compliance
- Use /tmp/opencode, not /tmp

## Architecture

The project uses a modular design with:

- **Core**: `SensorThingsService` class as the central API
- **DAO layer**: Data access objects for CRUD operations
- **Models**: Data model implementations (SensorThingsV11Sensing, SensorThingsV11Tasking, SensorThingsV20Core, etc.)
- **Query**: Fluent query API for OData-style filtering
- **Utils**: Helper classes for MQTT, token management, and common operations

## Testing

Tests are written using JUnit 5 and located in `src/test/java/`. Run with `./mvnw test`.

## CI/CD

The project uses GitHub Actions for builds. See `.github/workflows/` for configurations.

