# Requirements

## Web app
- De web-app moet lokaal bereikbaar zijn op: [http://localhost:8080](http://localhost:8080)
- Startcommando: `./gradlew :apps:web:wasmJsBrowserDevelopmentRun`

## Health data integraties
- Android gebruikt Health Connect via module `integration/health-connect` (Android-only).
- iOS gebruikt Apple HealthKit via module `integration/healthkit` (iOS-only).
- Beide platformen mappen data naar het canonieke model in `core/health`.
- Module-communicatie is event-based via gedeelde event-contracten in `core/health` voor realtime sync zonder DB polling.
