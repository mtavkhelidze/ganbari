# Cassandra Setup

## Queries

1. Active Nikka-s → generate YaruKoto
2. Active Nikka-s by Domain → filtered generation
3. YaruKoto for today
4. YaruKoto for past N days → completion graph
5. Kiroku for a specific Nikka → full history
6. Completion rate per Nikka → done vs scheduled
7. Streaks → consecutive completions
8. All Kiroku for a given day → audit log

## Schema

```cassandraql
-- Genral ladger, ultimate source of truth about the history
CREATE TABLE kiroku
(
    entity_data MAP<TEXT, TEXT>,
    entity_id   UUID,
    entity_type TEXT,
    id          UUID,
    ts          TIMESTAMP, -- in UTC
    variant     TEXT,
    PRIMARY KEY ((entity_type, entity_id), ts, id)
) WITH CLUSTERING ORDER BY (ts ASC, id ASC);
```

### Draft

```
-- 1) Query 1,2: active Nikka by domain
partition: domain_id
cluster:   nikka_id

-- 2) Query 3,4: yarukoto by date
partition: on (date)
cluster:   yarukoto_id

-- 3) Query 5,6,7: kiroku by subject
partition: nikka_id
cluster:   on (date), kiroku_id

-- 4) Query 8: kiroku by date
partition: on (date)
cluster:   kiroku_id
```

* on (kiroku_id) → queries 3, 4, 8
* nikka_id → queries 5, 6, 7
* domain_id → query 2
