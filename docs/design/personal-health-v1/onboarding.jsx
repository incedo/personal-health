/* Personal Health — Onboarding flow.
   9 steps, mobile-first. Each step is a standalone component so we can
   render them all side-by-side in the design canvas. State is local to
   the parent <OnboardingFlow>; in a real app this would post each step
   to the backend. */

const { useState } = React;

/* ───────────── Shared chrome ─────────────
   Stepper bar at top + sticky CTA at bottom. Each screen receives its
   own slot for the body. */
const OnbShell = ({ step, total, title, subtitle, children, primary, primaryDisabled, onPrimary, secondary, onSecondary, onBack, lang = "nl", muted = false }) => {
  const isNL = lang === "nl";
  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", background: muted ? "var(--ph-bg)" : "var(--ph-surface)" }}>
      {/* Top: progress + back */}
      <div style={{ display: "flex", alignItems: "center", gap: 12, padding: "16px 18px 8px" }}>
        {step > 1 && onBack ? (
          <IconButton icon="chevronRight" label="back" onClick={onBack} style={{ transform: "rotate(180deg)" }} />
        ) : <div style={{ width: 36 }} />}
        <div style={{ flex: 1, display: "flex", gap: 4 }}>
          {Array.from({ length: total }).map((_, i) => (
            <div key={i} style={{
              flex: 1, height: 3, borderRadius: 2,
              background: i < step ? "var(--ph-primary)" : "var(--ph-surface-muted)",
              transition: "background var(--ph-motion-base) var(--ph-ease-emph)",
            }} />
          ))}
        </div>
        <span className="ph-mono" style={{ fontSize: 11, color: "var(--ph-text-muted)", minWidth: 32, textAlign: "right" }}>
          {step}/{total}
        </span>
      </div>

      {/* Body */}
      <div style={{ flex: 1, overflow: "auto", padding: "12px 22px 20px" }}>
        {title && <div className="ph-h1" style={{ marginTop: 6 }}>{title}</div>}
        {subtitle && <div className="ph-body" style={{ color: "var(--ph-text-muted)", marginTop: 8, marginBottom: 18 }}>{subtitle}</div>}
        <div>{children}</div>
      </div>

      {/* Bottom CTA */}
      <div style={{
        padding: "12px 22px 22px", background: muted ? "var(--ph-bg)" : "var(--ph-surface)",
        borderTop: "1px solid var(--ph-divider)",
        display: "flex", flexDirection: "column", gap: 8,
      }}>
        {primary && (
          <Button
            full size="lg"
            iconRight={primary.icon || "chevronRight"}
            disabled={primaryDisabled}
            onClick={onPrimary}
          >{primary.label}</Button>
        )}
        {secondary && (
          <Button variant="ghost" full size="md" onClick={onSecondary}>{secondary}</Button>
        )}
      </div>
    </div>
  );
};

/* ───────────── Step 1: Welcome ─────────────
   Hero with brand + value prop. No form yet. */
const OnbWelcome = ({ onNext, onSkip, lang = "nl" }) => {
  const isNL = lang === "nl";
  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", background: "var(--ph-bg)" }}>
      {/* Hero */}
      <div style={{
        flex: 1, position: "relative", overflow: "hidden",
        background: "linear-gradient(160deg, var(--ph-primary-soft), var(--ph-surface))",
        display: "flex", flexDirection: "column", justifyContent: "center", alignItems: "center",
        padding: "40px 28px",
      }}>
        {/* Abstract topo background */}
        <svg viewBox="0 0 390 600" preserveAspectRatio="xMidYMid slice" style={{ position: "absolute", inset: 0, width: "100%", height: "100%", opacity: 0.5 }}>
          {[120, 180, 240, 300, 360, 420, 480].map((y, i) => (
            <path key={i} d={`M -20 ${y} Q 80 ${y - 18} 195 ${y - 6} T 410 ${y + 8}`}
                  stroke="var(--ph-primary)" strokeWidth="0.6" fill="none" opacity={0.18 + i * 0.04} />
          ))}
        </svg>

        <div style={{ position: "relative", textAlign: "center" }}>
          <div style={{
            width: 72, height: 72, borderRadius: 22,
            background: "linear-gradient(135deg, var(--ph-primary), var(--ph-accent))",
            display: "flex", alignItems: "center", justifyContent: "center",
            margin: "0 auto 24px", color: "#fff",
            boxShadow: "0 18px 48px -12px color-mix(in oklch, var(--ph-primary) 50%, transparent)",
          }}>
            <Icon name="leaf" size={32} />
          </div>
          <div className="ph-h1" style={{ fontSize: 32, lineHeight: 1.1, marginBottom: 12 }}>
            {isNL ? "Welkom" : "Welcome"}
          </div>
          <div className="ph-body" style={{ color: "var(--ph-text-muted)", fontSize: 16, lineHeight: 1.5, maxWidth: 320, margin: "0 auto" }}>
            {isNL
              ? "Een rustige, persoonlijke gids voor je gezondheid op de lange termijn — getraind op jouw data, jouw tempo."
              : "A calm, personal guide for your long-term health — trained on your data, at your pace."}
          </div>
        </div>
      </div>

      {/* Pillars */}
      <div style={{ padding: "24px 22px 8px" }}>
        {[
          ["heart", isNL ? "Begrijpt jouw lichaam" : "Understands your body", isNL ? "HRV, slaap, training en voeding samen." : "HRV, sleep, training and nutrition together."],
          ["target", isNL ? "Plan dat zich aanpast" : "Plan that adapts", isNL ? "Te weinig hersteld? We schalen automatisch." : "Not recovered? We scale automatically."],
          ["sparkles", isNL ? "Privé eerst" : "Privacy first", isNL ? "Jij beslist wat je deelt." : "You decide what you share."],
        ].map(([icn, t, d], i) => (
          <div key={i} style={{ display: "flex", gap: 14, padding: "10px 0", alignItems: "flex-start" }}>
            <div style={{
              width: 36, height: 36, borderRadius: 10,
              background: "var(--ph-primary-soft)", color: "var(--ph-primary)",
              display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0,
            }}>
              <Icon name={icn} size={18} />
            </div>
            <div>
              <div style={{ fontSize: 14, fontWeight: 600, marginBottom: 2 }}>{t}</div>
              <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", lineHeight: 1.4 }}>{d}</div>
            </div>
          </div>
        ))}
      </div>

      <div style={{ padding: "16px 22px 24px", display: "flex", flexDirection: "column", gap: 10 }}>
        <Button full size="lg" iconRight="chevronRight" onClick={onNext}>
          {isNL ? "Aan de slag" : "Get started"}
        </Button>
        <Button variant="ghost" full size="md" onClick={onSkip}>
          {isNL ? "Ik heb al een account" : "I already have an account"}
        </Button>
      </div>
    </div>
  );
};

/* ───────────── Step 2: Goals (multi-select cards) ───────────── */
const OnbGoals = ({ value = [], onChange, onNext, onBack, lang = "nl" }) => {
  const isNL = lang === "nl";
  const opts = [
    { key: "longevity", icon: "leaf", title: isNL ? "Langer gezond leven" : "Live longer, healthier", sub: isNL ? "VO2max, HRV, herstel" : "VO2max, HRV, recovery" },
    { key: "strength", icon: "dumbbell", title: isNL ? "Kracht opbouwen" : "Build strength", sub: isNL ? "Spiermassa, PR's, kracht" : "Muscle, PRs, power" },
    { key: "endurance", icon: "footprints", title: isNL ? "Conditie verbeteren" : "Improve endurance", sub: isNL ? "Hardlopen, fietsen, Z2" : "Running, cycling, Z2" },
    { key: "weight", icon: "trending", title: isNL ? "Lichaamssamenstelling" : "Body composition", sub: isNL ? "Vet kwijt, spier behouden" : "Fat down, muscle up" },
    { key: "sleep", icon: "moon", title: isNL ? "Beter slapen" : "Sleep better", sub: isNL ? "Diepere, regelmatigere slaap" : "Deeper, more regular sleep" },
    { key: "stress", icon: "heart", title: isNL ? "Stress & mentaal" : "Stress & mind", sub: isNL ? "Rust en focus" : "Calm and focus" },
  ];
  const toggle = key => onChange(value.includes(key) ? value.filter(k => k !== key) : [...value, key]);

  return (
    <OnbShell
      step={2} total={9} lang={lang}
      title={isNL ? "Wat wil je bereiken?" : "What do you want?"}
      subtitle={isNL ? "Kies één of meerdere — we passen het plan hierop aan." : "Pick one or more — we tune the plan to this."}
      primary={{ label: isNL ? "Volgende" : "Continue" }}
      primaryDisabled={value.length === 0}
      onPrimary={onNext}
      onBack={onBack}
    >
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
        {opts.map(o => {
          const sel = value.includes(o.key);
          return (
            <button key={o.key} onClick={() => toggle(o.key)} style={{
              all: "unset", cursor: "pointer",
              padding: 14, borderRadius: "var(--ph-r-md)",
              border: `2px solid ${sel ? "var(--ph-primary)" : "var(--ph-border)"}`,
              background: sel ? "var(--ph-primary-soft)" : "var(--ph-surface)",
              transition: "all var(--ph-motion-base) var(--ph-ease-emph)",
              display: "flex", flexDirection: "column", gap: 8, minHeight: 110,
            }}>
              <div style={{
                width: 32, height: 32, borderRadius: 8,
                background: sel ? "var(--ph-primary)" : "var(--ph-surface-muted)",
                color: sel ? "#fff" : "var(--ph-primary)",
                display: "flex", alignItems: "center", justifyContent: "center",
              }}>
                <Icon name={o.icon} size={16} />
              </div>
              <div>
                <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 2 }}>{o.title}</div>
                <div className="ph-caption" style={{ fontSize: 11 }}>{o.sub}</div>
              </div>
            </button>
          );
        })}
      </div>
    </OnbShell>
  );
};

/* ───────────── Step 3: About you (gender, age, height, weight) ───────────── */
const OnbAbout = ({ value = {}, onChange, onNext, onBack, lang = "nl" }) => {
  const isNL = lang === "nl";
  const set = (k, v) => onChange({ ...value, [k]: v });
  const genderOpts = [
    { v: "f", l: isNL ? "Vrouw" : "Female" },
    { v: "m", l: isNL ? "Man" : "Male" },
    { v: "x", l: isNL ? "Anders" : "Other" },
  ];

  return (
    <OnbShell
      step={3} total={9} lang={lang}
      title={isNL ? "Over jou" : "About you"}
      subtitle={isNL ? "Voor accurate kcal-, kracht- en VO2max-berekeningen." : "For accurate kcal, strength and VO2max math."}
      primary={{ label: isNL ? "Volgende" : "Continue" }}
      primaryDisabled={!value.age || !value.height || !value.weight || !value.gender}
      onPrimary={onNext}
      onBack={onBack}
    >
      <div style={{ display: "flex", flexDirection: "column", gap: 22 }}>
        <div>
          <div className="ph-label" style={{ marginBottom: 8 }}>{isNL ? "GESLACHT" : "GENDER"}</div>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 8 }}>
            {genderOpts.map(g => (
              <button key={g.v} onClick={() => set("gender", g.v)} style={{
                all: "unset", cursor: "pointer", textAlign: "center",
                padding: "14px 0", borderRadius: "var(--ph-r-md)",
                border: `2px solid ${value.gender === g.v ? "var(--ph-primary)" : "var(--ph-border)"}`,
                background: value.gender === g.v ? "var(--ph-primary-soft)" : "var(--ph-surface)",
                color: value.gender === g.v ? "var(--ph-primary)" : "var(--ph-text)",
                fontSize: 14, fontWeight: 500,
              }}>{g.l}</button>
            ))}
          </div>
        </div>

        <FieldRow label={isNL ? "LEEFTIJD" : "AGE"} value={value.age} unit={isNL ? "jaar" : "yr"} placeholder="34" onChange={v => set("age", v)} max={3} />
        <FieldRow label={isNL ? "LENGTE" : "HEIGHT"} value={value.height} unit="cm" placeholder="178" onChange={v => set("height", v)} max={3} />
        <FieldRow label={isNL ? "GEWICHT" : "WEIGHT"} value={value.weight} unit="kg" placeholder="74.5" onChange={v => set("weight", v)} max={5} />
      </div>
    </OnbShell>
  );
};

/* Compact field row used by several onboarding steps */
const FieldRow = ({ label, value, unit, placeholder, onChange, max = 4 }) => (
  <div>
    <div className="ph-label" style={{ marginBottom: 8 }}>{label}</div>
    <div style={{
      display: "flex", alignItems: "baseline", gap: 8,
      padding: "12px 16px", borderRadius: "var(--ph-r-md)",
      border: "1px solid var(--ph-border)", background: "var(--ph-surface)",
    }}>
      <input
        type="text" inputMode="decimal" value={value || ""} placeholder={placeholder}
        maxLength={max} onChange={e => onChange(e.target.value)}
        style={{
          all: "unset", flex: 1, fontSize: 22, fontWeight: 600,
          fontFamily: "var(--ph-font-mono)", color: "var(--ph-text)",
        }}
      />
      <span style={{ fontSize: 14, color: "var(--ph-text-muted)" }}>{unit}</span>
    </div>
  </div>
);

/* ───────────── Step 4: Activity level ───────────── */
const OnbActivity = ({ value, onChange, onNext, onBack, lang = "nl" }) => {
  const isNL = lang === "nl";
  const opts = [
    { v: "starter", t: isNL ? "Beginner" : "Starter", d: isNL ? "Net begonnen of na lange pauze" : "New or returning after a long break", icn: "sparkles" },
    { v: "casual", t: isNL ? "Recreatief" : "Recreational", d: isNL ? "1–2× per week sport, basisconditie" : "1–2× per week, base fitness", icn: "footprints" },
    { v: "regular", t: isNL ? "Regelmatig" : "Regular", d: isNL ? "3–4× per week, gericht aan het trainen" : "3–4× per week, training with intent", icn: "activity" },
    { v: "athletic", t: isNL ? "Atletisch" : "Athletic", d: isNL ? "5+ per week, prestatiegericht" : "5+ per week, performance-focused", icn: "trending" },
  ];
  return (
    <OnbShell
      step={4} total={9} lang={lang}
      title={isNL ? "Hoe actief ben je?" : "How active are you?"}
      subtitle={isNL ? "Eerlijk antwoorden — we starten je op het juiste niveau." : "Be honest — we start you at the right level."}
      primary={{ label: isNL ? "Volgende" : "Continue" }}
      primaryDisabled={!value}
      onPrimary={onNext}
      onBack={onBack}
    >
      <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
        {opts.map(o => {
          const sel = value === o.v;
          return (
            <button key={o.v} onClick={() => onChange(o.v)} style={{
              all: "unset", cursor: "pointer",
              padding: 16, borderRadius: "var(--ph-r-md)",
              border: `2px solid ${sel ? "var(--ph-primary)" : "var(--ph-border)"}`,
              background: sel ? "var(--ph-primary-soft)" : "var(--ph-surface)",
              display: "flex", alignItems: "center", gap: 14,
            }}>
              <div style={{
                width: 40, height: 40, borderRadius: 10,
                background: sel ? "var(--ph-primary)" : "var(--ph-surface-muted)",
                color: sel ? "#fff" : "var(--ph-primary)",
                display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0,
              }}>
                <Icon name={o.icn} size={18} />
              </div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontSize: 15, fontWeight: 600 }}>{o.t}</div>
                <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 2 }}>{o.d}</div>
              </div>
              <div style={{
                width: 22, height: 22, borderRadius: "50%",
                border: `2px solid ${sel ? "var(--ph-primary)" : "var(--ph-border)"}`,
                background: sel ? "var(--ph-primary)" : "transparent",
                display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0,
              }}>
                {sel && <Icon name="check" size={12} color="#fff" strokeWidth={3} />}
              </div>
            </button>
          );
        })}
      </div>
    </OnbShell>
  );
};

/* ───────────── Step 5: Availability (days + hours) ───────────── */
const OnbAvailability = ({ value = { days: [], hours: 4 }, onChange, onNext, onBack, lang = "nl" }) => {
  const isNL = lang === "nl";
  const dayLabels = isNL ? ["M","D","W","D","V","Z","Z"] : ["M","T","W","T","F","S","S"];
  const fullDays = isNL ? ["Ma","Di","Wo","Do","Vr","Za","Zo"] : ["Mon","Tue","Wed","Thu","Fri","Sat","Sun"];
  const toggleDay = i => {
    const next = value.days.includes(i) ? value.days.filter(d => d !== i) : [...value.days, i];
    onChange({ ...value, days: next });
  };
  return (
    <OnbShell
      step={5} total={9} lang={lang}
      title={isNL ? "Wanneer kun je?" : "When can you?"}
      subtitle={isNL ? "We bouwen je weekplan rond deze beschikbaarheid." : "We build your week plan around this."}
      primary={{ label: isNL ? "Volgende" : "Continue" }}
      primaryDisabled={value.days.length === 0}
      onPrimary={onNext}
      onBack={onBack}
    >
      <div className="ph-label" style={{ marginBottom: 10 }}>{isNL ? "DAGEN PER WEEK" : "DAYS PER WEEK"}</div>
      <div style={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: 6, marginBottom: 24 }}>
        {dayLabels.map((d, i) => {
          const sel = value.days.includes(i);
          return (
            <button key={i} onClick={() => toggleDay(i)} style={{
              all: "unset", cursor: "pointer", textAlign: "center",
              padding: "14px 0", borderRadius: "var(--ph-r-md)",
              border: `2px solid ${sel ? "var(--ph-primary)" : "var(--ph-border)"}`,
              background: sel ? "var(--ph-primary)" : "var(--ph-surface)",
              color: sel ? "#fff" : "var(--ph-text)",
              fontSize: 14, fontWeight: 600,
            }}>{d}</button>
          );
        })}
      </div>
      {value.days.length > 0 && (
        <div className="ph-caption" style={{ marginBottom: 24 }}>
          {value.days.map(i => fullDays[i]).join(" · ")}
        </div>
      )}

      <div className="ph-label" style={{ marginBottom: 10 }}>{isNL ? "UREN PER WEEK" : "HOURS PER WEEK"}</div>
      <div style={{
        padding: 18, borderRadius: "var(--ph-r-md)", background: "var(--ph-surface-muted)",
      }}>
        <div style={{ fontSize: 36, fontWeight: 700, fontFamily: "var(--ph-font-mono)", color: "var(--ph-primary)", textAlign: "center", marginBottom: 12 }}>
          {value.hours}<span style={{ fontSize: 16, color: "var(--ph-text-muted)", fontWeight: 400, marginLeft: 4 }}>{isNL ? "u" : "h"}</span>
        </div>
        <input type="range" min={1} max={20} step={1} value={value.hours}
          onChange={e => onChange({ ...value, hours: parseInt(e.target.value) })}
          style={{ width: "100%", accentColor: "var(--ph-primary)" }}
        />
        <div style={{ display: "flex", justifyContent: "space-between", fontSize: 11, color: "var(--ph-text-muted)", marginTop: 6 }}>
          <span>1{isNL ? "u" : "h"}</span><span>10{isNL ? "u" : "h"}</span><span>20{isNL ? "u" : "h"}</span>
        </div>
      </div>
    </OnbShell>
  );
};

/* ───────────── Step 6: Devices / wearables ───────────── */
const OnbDevices = ({ value = [], onChange, onNext, onBack, onSkip, lang = "nl" }) => {
  const isNL = lang === "nl";
  const devices = [
    { v: "apple_watch", t: "Apple Watch", d: isNL ? "HRV, hartslag, slaap, training" : "HRV, HR, sleep, workouts", brand: "linear-gradient(135deg, #1a1a1a, #444)" },
    { v: "garmin", t: "Garmin", d: isNL ? "GPS, hartslag, training, herstel" : "GPS, HR, workouts, recovery", brand: "linear-gradient(135deg, #1a4970, #006cc1)" },
    { v: "whoop", t: "Whoop", d: isNL ? "HRV, herstel, strain, slaap" : "HRV, recovery, strain, sleep", brand: "linear-gradient(135deg, #000, #333)" },
    { v: "oura", t: "Oura Ring", d: isNL ? "HRV, slaap, lichaamstemperatuur" : "HRV, sleep, body temperature", brand: "linear-gradient(135deg, #4a4a4a, #1a1a1a)" },
    { v: "apple_health", t: "Apple Health", d: isNL ? "Centrale gegevens van iPhone" : "Central data from iPhone", brand: "linear-gradient(135deg, #ff3b30, #ff9500)" },
    { v: "google_fit", t: "Google Fit", d: isNL ? "Centrale gegevens van Android" : "Central data from Android", brand: "linear-gradient(135deg, #4285f4, #34a853)" },
    { v: "scale", t: isNL ? "Slimme weegschaal" : "Smart scale", d: isNL ? "Withings, Garmin, Renpho" : "Withings, Garmin, Renpho", brand: "linear-gradient(135deg, #6b8a4f, #3f5b34)" },
  ];
  const toggle = v => onChange(value.includes(v) ? value.filter(x => x !== v) : [...value, v]);

  return (
    <OnbShell
      step={6} total={9} lang={lang}
      title={isNL ? "Verbind je apparaten" : "Connect your devices"}
      subtitle={isNL ? "Hoe meer we lezen, hoe beter we coachen. Je kunt dit later wijzigen." : "More data means better coaching. You can change this later."}
      primary={{ label: isNL ? "Volgende" : "Continue" }}
      onPrimary={onNext}
      onBack={onBack}
      secondary={isNL ? "Overslaan, ik koppel later" : "Skip, I'll connect later"}
      onSecondary={onSkip}
    >
      <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
        {devices.map(d => {
          const sel = value.includes(d.v);
          return (
            <button key={d.v} onClick={() => toggle(d.v)} style={{
              all: "unset", cursor: "pointer",
              padding: 14, borderRadius: "var(--ph-r-md)",
              border: `1px solid ${sel ? "var(--ph-primary)" : "var(--ph-border)"}`,
              background: sel ? "var(--ph-primary-soft)" : "var(--ph-surface)",
              display: "flex", alignItems: "center", gap: 14,
            }}>
              <div style={{
                width: 40, height: 40, borderRadius: 10, background: d.brand,
                display: "flex", alignItems: "center", justifyContent: "center", color: "#fff",
                flexShrink: 0, fontSize: 13, fontWeight: 700,
              }}>{d.t.split(" ").map(w => w[0]).join("").slice(0, 2)}</div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontSize: 14, fontWeight: 600 }}>{d.t}</div>
                <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 2 }}>{d.d}</div>
              </div>
              {sel ? (
                <Tag tone="primary" icon="check">{isNL ? "Verbonden" : "Linked"}</Tag>
              ) : (
                <span style={{ fontSize: 13, fontWeight: 500, color: "var(--ph-primary)" }}>
                  {isNL ? "Verbind" : "Link"}
                </span>
              )}
            </button>
          );
        })}
      </div>
    </OnbShell>
  );
};

/* ───────────── Step 7: Diet preferences + restrictions ───────────── */
const OnbDiet = ({ value = { style: null, restrictions: [] }, onChange, onNext, onBack, lang = "nl" }) => {
  const isNL = lang === "nl";
  const styles = [
    { v: "balanced", t: isNL ? "Gevarieerd" : "Balanced", icn: "apple" },
    { v: "high_protein", t: isNL ? "Eiwitrijk" : "High-protein", icn: "dumbbell" },
    { v: "mediterranean", t: isNL ? "Mediterraans" : "Mediterranean", icn: "leaf" },
    { v: "vegetarian", t: isNL ? "Vegetarisch" : "Vegetarian", icn: "leaf" },
    { v: "vegan", t: "Vegan", icn: "leaf" },
    { v: "low_carb", t: isNL ? "Koolhydraatarm" : "Low-carb", icn: "flame" },
  ];
  const restrictions = [
    isNL ? "Lactose" : "Lactose",
    isNL ? "Gluten" : "Gluten",
    isNL ? "Noten" : "Nuts",
    isNL ? "Soja" : "Soy",
    isNL ? "Schaaldieren" : "Shellfish",
    isNL ? "Eieren" : "Eggs",
  ];
  const toggleR = r => onChange({ ...value, restrictions: value.restrictions.includes(r) ? value.restrictions.filter(x => x !== r) : [...value.restrictions, r] });

  return (
    <OnbShell
      step={7} total={9} lang={lang}
      title={isNL ? "Voeding" : "Nutrition"}
      subtitle={isNL ? "Voor maaltijdsuggesties en macroberekeningen." : "Used for meal suggestions and macro math."}
      primary={{ label: isNL ? "Volgende" : "Continue" }}
      primaryDisabled={!value.style}
      onPrimary={onNext}
      onBack={onBack}
    >
      <div className="ph-label" style={{ marginBottom: 10 }}>{isNL ? "VOEDINGSSTIJL" : "EATING STYLE"}</div>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8, marginBottom: 24 }}>
        {styles.map(s => {
          const sel = value.style === s.v;
          return (
            <button key={s.v} onClick={() => onChange({ ...value, style: s.v })} style={{
              all: "unset", cursor: "pointer",
              padding: 12, borderRadius: "var(--ph-r-md)",
              border: `2px solid ${sel ? "var(--ph-primary)" : "var(--ph-border)"}`,
              background: sel ? "var(--ph-primary-soft)" : "var(--ph-surface)",
              display: "flex", alignItems: "center", gap: 10,
            }}>
              <Icon name={s.icn} size={18} color={sel ? "var(--ph-primary)" : "var(--ph-text-muted)"} />
              <span style={{ fontSize: 13, fontWeight: 500, color: sel ? "var(--ph-primary)" : "var(--ph-text)" }}>{s.t}</span>
            </button>
          );
        })}
      </div>

      <div className="ph-label" style={{ marginBottom: 10 }}>{isNL ? "ALLERGIEËN OF MIJDEN" : "ALLERGIES OR AVOID"}</div>
      <div style={{ display: "flex", flexWrap: "wrap", gap: 8 }}>
        {restrictions.map(r => {
          const sel = value.restrictions.includes(r);
          return (
            <button key={r} onClick={() => toggleR(r)} style={{
              all: "unset", cursor: "pointer",
              padding: "8px 14px", borderRadius: 999,
              border: `1px solid ${sel ? "var(--ph-warning, #c08a3e)" : "var(--ph-border)"}`,
              background: sel ? "color-mix(in oklch, var(--ph-warning, #c08a3e) 14%, transparent)" : "var(--ph-surface)",
              color: sel ? "var(--ph-warning, #c08a3e)" : "var(--ph-text)",
              fontSize: 13, fontWeight: 500,
              display: "flex", alignItems: "center", gap: 6,
            }}>
              {sel && <Icon name="check" size={12} strokeWidth={3} />}
              {r}
            </button>
          );
        })}
      </div>
    </OnbShell>
  );
};

/* ───────────── Step 8: Baseline measurement (RHR + optional HRV) ───────────── */
const OnbBaseline = ({ value = {}, onChange, onNext, onBack, onSkip, lang = "nl" }) => {
  const isNL = lang === "nl";
  const set = (k, v) => onChange({ ...value, [k]: v });
  return (
    <OnbShell
      step={8} total={9} lang={lang}
      title={isNL ? "Basismeting" : "Baseline"}
      subtitle={isNL ? "Jouw startpunt — we vergelijken hier alles op." : "Your starting point — everything compares against this."}
      primary={{ label: isNL ? "Volgende" : "Continue" }}
      onPrimary={onNext}
      onBack={onBack}
      secondary={isNL ? "Overslaan, meten we deze week" : "Skip, we'll measure this week"}
      onSecondary={onSkip}
    >
      <Card padding={16} style={{ marginBottom: 20, background: "var(--ph-primary-soft)", border: "none" }}>
        <div style={{ display: "flex", gap: 12, alignItems: "flex-start" }}>
          <Icon name="sparkles" size={18} color="var(--ph-primary)" style={{ marginTop: 2 }} />
          <div className="ph-body-sm" style={{ color: "var(--ph-text)", lineHeight: 1.5 }}>
            {isNL
              ? "Meet 's morgens vlak na het wakker worden. Lig stil voor 1 minuut en meet je rusthartslag."
              : "Measure right after waking. Lie still for 1 min and measure resting heart rate."}
          </div>
        </div>
      </Card>

      <FieldRow label={isNL ? "RUSTHARTSLAG (RHR)" : "RESTING HEART RATE (RHR)"} value={value.rhr} unit="bpm" placeholder="58" onChange={v => set("rhr", v)} max={3} />
      <div style={{ height: 16 }} />
      <FieldRow label={isNL ? "HRV (OPTIONEEL)" : "HRV (OPTIONAL)"} value={value.hrv} unit="ms" placeholder="68" onChange={v => set("hrv", v)} max={3} />

      <div style={{ marginTop: 16, padding: 12, borderRadius: "var(--ph-r-sm)", background: "var(--ph-surface-muted)" }}>
        <div className="ph-caption" style={{ lineHeight: 1.5 }}>
          {isNL
            ? "Geen wearable? Tel 30 seconden je hartslag in je nek of pols en vermenigvuldig met 2."
            : "No wearable? Count your pulse for 30 sec in neck or wrist and multiply by 2."}
        </div>
      </div>
    </OnbShell>
  );
};

/* ───────────── Step 9: Done — your plan is ready ───────────── */
const OnbDone = ({ data, onFinish, lang = "nl" }) => {
  const isNL = lang === "nl";
  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", background: "var(--ph-bg)" }}>
      <div style={{ flex: 1, position: "relative", overflow: "hidden", padding: "40px 22px 24px" }}>
        {/* Confetti rings */}
        <svg viewBox="0 0 390 600" preserveAspectRatio="xMidYMid slice" style={{ position: "absolute", inset: 0, width: "100%", height: "100%", opacity: 0.5 }}>
          {Array.from({ length: 14 }).map((_, i) => {
            const x = (i * 37 + 23) % 390;
            const y = (i * 71 + 41) % 600;
            return (
              <circle key={i} cx={x} cy={y} r={3 + (i % 4)}
                fill={["var(--ph-primary)", "var(--ph-data-3)", "var(--ph-data-4)", "var(--ph-accent)"][i % 4]}
                opacity={0.25 + (i % 4) * 0.1} />
            );
          })}
        </svg>

        <div style={{ position: "relative", textAlign: "center", marginTop: 40 }}>
          <div style={{
            width: 88, height: 88, borderRadius: "50%",
            background: "linear-gradient(135deg, var(--ph-primary), var(--ph-accent))",
            display: "flex", alignItems: "center", justifyContent: "center",
            margin: "0 auto 24px", color: "#fff",
            boxShadow: "0 18px 48px -12px color-mix(in oklch, var(--ph-primary) 50%, transparent)",
          }}>
            <Icon name="check" size={42} strokeWidth={3} />
          </div>
          <div className="ph-h1" style={{ fontSize: 30, lineHeight: 1.15, marginBottom: 12 }}>
            {isNL ? "Je plan staat klaar" : "Your plan is ready"}
          </div>
          <div className="ph-body" style={{ color: "var(--ph-text-muted)", fontSize: 15, maxWidth: 320, margin: "0 auto" }}>
            {isNL
              ? "Op basis van je doelen en beschikbaarheid hebben we je eerste week opgesteld."
              : "Based on your goals and availability we've drafted your first week."}
          </div>
        </div>

        <Card padding={18} style={{ marginTop: 32, background: "var(--ph-surface)" }}>
          <div className="ph-label" style={{ marginBottom: 10 }}>{isNL ? "JE EERSTE WEEK" : "YOUR FIRST WEEK"}</div>
          <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
            {[
              ["dumbbell", isNL ? "3× kracht" : "3× strength", isNL ? "Push · Pull · Benen" : "Push · Pull · Legs"],
              ["footprints", isNL ? "2× cardio" : "2× cardio", isNL ? "1× Z2 + 1× intervallen" : "1× Z2 + 1× intervals"],
              ["heart", isNL ? "1× mobility" : "1× mobility", isNL ? "20 min, herstel-dag" : "20 min, recovery day"],
              ["apple", isNL ? "Voeding" : "Nutrition", isNL ? "≈ 2.350 kcal · 165g eiwit" : "≈ 2,350 kcal · 165g protein"],
            ].map(([icn, t, d], i) => (
              <div key={i} style={{ display: "flex", gap: 12, alignItems: "center" }}>
                <div style={{
                  width: 36, height: 36, borderRadius: 10, background: "var(--ph-primary-soft)",
                  color: "var(--ph-primary)", display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0,
                }}>
                  <Icon name={icn} size={16} />
                </div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: 13, fontWeight: 600 }}>{t}</div>
                  <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)" }}>{d}</div>
                </div>
              </div>
            ))}
          </div>
        </Card>
      </div>

      <div style={{ padding: "16px 22px 24px", display: "flex", flexDirection: "column", gap: 10 }}>
        <Button full size="lg" iconRight="chevronRight" onClick={onFinish}>
          {isNL ? "Bekijk mijn dag" : "See my day"}
        </Button>
        <Button variant="ghost" full size="md">
          {isNL ? "Pas plan aan" : "Tweak plan"}
        </Button>
      </div>
    </div>
  );
};

Object.assign(window, {
  OnbWelcome, OnbGoals, OnbAbout, OnbActivity, OnbAvailability,
  OnbDevices, OnbDiet, OnbBaseline, OnbDone, OnbShell, FieldRow,
});
