# Kleisli Combinators (Cats) — Minimal Core & Derivations

## Core primitives

| Concept | Operator | Meaning |
|--------|----------|---------|
| Composition | `>>>` | Sequential composition |
| Pure transform | `map` | Transform result |
| Effectful chaining | `flatMap` | Dependent chaining |
| Input transform | `local` | Transform input |

---

## Everything else (derivable)

### Composition

```scala
f >>> g
≡ Kleisli(a => f.run(a).flatMap(g.run))
```

---

### map

```scala
k.map(f)
≡ Kleisli(a => k.run(a).map(f))
```

---

### flatMap

```scala
k.flatMap(f)
≡ Kleisli(a => k.run(a).flatMap(b => f(b).run(a)))
```

---

### local

```scala
k.local(f)
≡ Kleisli(c => k.run(f(c)))
```

---

### dimap

```scala
k.dimap(f)(g)
≡ k.local(f).map(g)
```

---

### andThenF

```scala
k.andThenF(f)
≡ k >>> Kleisli(f)
```

---

### compose / <<<

```scala
f <<< g
≡ g >>> f
```

---

### mapF

```scala
k.mapF(f)
≡ Kleisli(a => f(k.run(a)))
```

---

## Arrow combinators (intuition)

These are structural and can be derived via composition + tuple manipulation:

| Name | Idea |
|------|------|
| `first` | apply to first element |
| `second` | apply to second |
| `***` | parallel on tuple |
| `&&&` | duplicate input |

---

## One-line mental model

> Kleisli = `A => F[B]` + lawful composition via `flatMap`
