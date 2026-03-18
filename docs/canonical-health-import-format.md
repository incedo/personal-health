# Canonical Health Import Format

This format is intended for browser import and other non-native ingestion paths that cannot read Health Connect or HealthKit directly.

The document represents canonical data model parts from `core/health`:

- `window`
  - import context for the time span shown in the UI
- `records`
  - canonical `HealthRecord` values
- each record maps to:
  - `id`
  - `metric`
  - `value`
  - `unit`
  - `startEpochMillis`
  - `endEpochMillis`
  - `source`
  - `metadata`

## JSON shape

```json
{
  "version": "1",
  "exportedAtEpochMillis": 1768473600000,
  "window": {
    "startEpochMillis": 1768435200000,
    "endEpochMillis": 1768521599000
  },
  "records": [
    {
      "id": "steps-09",
      "metric": "STEPS",
      "value": 1240,
      "unit": "count",
      "startEpochMillis": 1768467600000,
      "endEpochMillis": 1768471199000,
      "source": "UNKNOWN",
      "metadata": {
        "origin": "web-csv-import",
        "label": "09:00"
      }
    }
  ]
}
```

## Field rules

- `version`
  - required string
  - current value: `"1"`
- `exportedAtEpochMillis`
  - optional long
  - timestamp for the export/import payload itself
- `window.startEpochMillis`
  - required long
  - start of the imported day or reporting window
- `window.endEpochMillis`
  - required long
  - end of the imported day or reporting window
- `records`
  - required array
  - each item must be a canonical `HealthRecord`

## Canonical enum values

### `metric`

Allowed values currently include:

- `STEPS`
- `HEART_RATE_BPM`
- `SLEEP_DURATION_MINUTES`
- `ACTIVE_ENERGY_KCAL`
- `BODY_WEIGHT_KG`

### `source`

Allowed values:

- `HEALTH_CONNECT`
- `HEALTHKIT`
- `UNKNOWN`

For browser imports, `UNKNOWN` is the default unless the upstream export has a trustworthy mapped source.

## Step import guidance

For the step detail screen, the most useful import shape is one `STEPS` record per hour bucket:

- `unit` should be `count`
- `value` should be the number of steps in that bucket
- `startEpochMillis` and `endEpochMillis` should mark that hour span

This lets the web app rebuild:

- today total steps
- condensed home preview buckets
- the detailed steps-per-hour graph

## Notes

- This is a canonical representation, not a raw Health Connect or HealthKit export.
- Upstream platform exports should be mapped into canonical values before import.
- Unknown extra fields are ignored by the current parser.
