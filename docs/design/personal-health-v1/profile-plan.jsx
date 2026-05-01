/* Personal Health — Profile, Plan, and Onboarding flow shell.
   Profile: identity, vitals, devices, goals, privacy, prefs (mobile + desktop).
   Plan: week view, today, periodisering (mesocycle), adapt-to-recovery banner.
   OnboardingFlow: stitches the existing Onb* steps into a real 9-step flow
   with a stateful step counter and forward/back nav.
*/

const { useState: useStatePP } = React;

/* i18n — small per-screen dictionaries to avoid bloating screens.js */
const PP_S = {
  nl: {
    profile: "Profiel", you: "Jij", devices: "Apparaten", goals: "Doelen",
    privacy: "Privacy", prefs: "Voorkeuren", export: "Exporteren",
    edit: "Bewerken", connected: "Verbonden", lastSync: "Laatste sync",
    plan: "Plan", thisWeek: "Deze week", today: "Vandaag",
    weekLoad: "Belasting deze week", recovery: "Herstel-aanpassing",
    cycle: "Cyclus", week: "Week", deload: "Deload",
    mesocycle: "Mesocyclus", phase: "Fase", build: "Opbouw",
    move: "Verplaats", swap: "Wissel", details: "Details",
    members: "leden", started: "gestart",
    age: "Leeftijd", height: "Lengte", weight: "Gewicht", rhr: "Rust HR", hrv: "HRV",
    activity: "Activiteit", level: "Niveau", availability: "Beschikbaarheid",
    daysWeek: "dagen/week", hoursWeek: "uur/week",
    rename: "Naam", units: "Eenheden", language: "Taal",
    download: "Download mijn data", erase: "Wis account",
    diet: "Voeding", restrictions: "Beperkingen",
    visibility: "Zichtbaarheid", workouts: "Trainingen", food: "Voeding",
    weightData: "Gewicht", restData: "Slaap & herstel",
    onlyMe: "Alleen ik", friends: "Vrienden", everyone: "Iedereen",
    feel: "Hoe voel je je vandaag?",
    autoTune: "Plan past zich aan",
    targetVolume: "Doel volume", actualVolume: "Logged",
  },
  en: {
    profile: "Profile", you: "You", devices: "Devices", goals: "Goals",
    privacy: "Privacy", prefs: "Preferences", export: "Export",
    edit: "Edit", connected: "Connected", lastSync: "Last sync",
    plan: "Plan", thisWeek: "This week", today: "Today",
    weekLoad: "Load this week", recovery: "Recovery adapt",
    cycle: "Cycle", week: "Week", deload: "Deload",
    mesocycle: "Mesocycle", phase: "Phase", build: "Build",
    move: "Move", swap: "Swap", details: "Details",
    members: "members", started: "started",
    age: "Age", height: "Height", weight: "Weight", rhr: "Resting HR", hrv: "HRV",
    activity: "Activity", level: "Level", availability: "Availability",
    daysWeek: "days/wk", hoursWeek: "hr/wk",
    rename: "Name", units: "Units", language: "Language",
    download: "Download my data", erase: "Delete account",
    diet: "Diet", restrictions: "Restrictions",
    visibility: "Visibility", workouts: "Workouts", food: "Food",
    weightData: "Weight", restData: "Sleep & recovery",
    onlyMe: "Only me", friends: "Friends", everyone: "Everyone",
    feel: "How do you feel today?",
    autoTune: "Plan adapts itself",
    targetVolume: "Target volume", actualVolume: "Logged",
  },
};
const ppT = (lang) => (k) => (PP_S[lang] && PP_S[lang][k]) || PP_S.en[k] || k;

const ppDensity = (d) => ({
  compact:     { gap: 10, pad: 14, vgap: 12 },
  comfortable: { gap: 16, pad: 20, vgap: 18 },
  spacious:    { gap: 22, pad: 26, vgap: 26 },
}[d] || { gap: 16, pad: 20, vgap: 18 });

/* ─────────────────────────────────────────────
   PROFILE SCREEN
   Identity + body stats + devices + goals + privacy + prefs.
   On mobile (data-grid-collapse) it stacks; on desktop, two columns.
   ───────────────────────────────────────────── */
const ProfileScreen = ({ lang = "nl", units = "metric", density = "comfortable" }) => {
  const t = ppT(lang);
  const D = ppDensity(density);
  const isNL = lang === "nl";

  const profile = {
    name: "Martijn de Vries", email: "martijn@example.nl",
    member: isNL ? "Lid sinds maart 2024" : "Member since March 2024",
    streak: 14,
  };
  const vitals = [
    [t("age"), "34", isNL ? "jaar" : "yr"],
    [t("height"), units === "imperial" ? "5'10\"" : "178", units === "imperial" ? "" : "cm"],
    [t("weight"), units === "imperial" ? "164.2" : "74.5", units === "imperial" ? "lb" : "kg"],
    [t("rhr"), "52", "bpm"],
    [t("hrv"), "68", "ms"],
    ["VO₂max", "47.2", "ml/kg/min"],
  ];
  const goals = [
    { v: "longevity", icon: "leaf", t: isNL ? "Langer gezond leven" : "Live longer healthier", primary: true },
    { v: "strength", icon: "dumbbell", t: isNL ? "Kracht opbouwen" : "Build strength" },
    { v: "endurance", icon: "footprints", t: isNL ? "Conditie verbeteren" : "Improve endurance" },
  ];
  const devices = [
    { name: "Apple Watch Series 9", brand: "linear-gradient(135deg,#1a1a1a,#444)", connected: true, last: "2 min", types: "HR · HRV · Sleep" },
    { name: "Withings Body+", brand: "linear-gradient(135deg,#6b8a4f,#3f5b34)", connected: true, last: isNL ? "vanmorgen" : "this morning", types: isNL ? "Gewicht · vetpercentage" : "Weight · body fat" },
    { name: "Oura Ring Gen3", brand: "linear-gradient(135deg,#4a4a4a,#1a1a1a)", connected: false, last: "—", types: "HRV · Sleep · Temp" },
  ];
  const privacy = [
    { key: "workouts", label: t("workouts"), value: "friends" },
    { key: "food", label: t("food"), value: "onlyMe" },
    { key: "weight", label: t("weightData"), value: "onlyMe" },
    { key: "rest", label: t("restData"), value: "friends" },
  ];

  return (
    <div style={{ height: "100%", overflow: "auto", background: "var(--ph-bg)" }}>
      <div style={{ padding: D.pad, maxWidth: 1100, margin: "0 auto", display: "flex", flexDirection: "column", gap: D.vgap }}>

        {/* Header */}
        <div data-mobile-stack style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", gap: 14 }}>
          <div>
            <div className="ph-caption">{t("profile")}</div>
            <div className="ph-h1" style={{ marginTop: 4 }}>{profile.name}</div>
            <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 4 }}>{profile.email} · {profile.member}</div>
          </div>
          <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
            <Button variant="ghost" icon="settings">{t("prefs")}</Button>
            <Button variant="secondary" icon="user">{t("edit")}</Button>
          </div>
        </div>

        {/* Identity hero card */}
        <Card padding={D.pad} style={{ background: "linear-gradient(135deg, var(--ph-primary-soft) 0%, var(--ph-surface) 60%)" }}>
          <div data-mobile-stack style={{ display: "flex", alignItems: "center", gap: 24 }}>
            <div style={{
              width: 96, height: 96, borderRadius: "50%",
              background: "linear-gradient(135deg, var(--ph-primary), var(--ph-accent))",
              color: "#fff", display: "flex", alignItems: "center", justifyContent: "center",
              fontSize: 32, fontWeight: 600, flexShrink: 0,
              boxShadow: "0 12px 32px -8px color-mix(in oklch, var(--ph-primary) 50%, transparent)",
            }}>M</div>
            <div style={{ flex: 1, minWidth: 220 }}>
              <div style={{ display: "flex", gap: 8, flexWrap: "wrap", marginBottom: 10 }}>
                <Tag tone="primary" icon="sparkles">{profile.streak} {isNL ? "dagen streak" : "day streak"}</Tag>
                <Tag tone="success" icon="check">{isNL ? "Goed hersteld" : "Recovered"}</Tag>
                <Tag icon="trending">VO₂max +1.4</Tag>
              </div>
              <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", maxWidth: 480 }}>
                {isNL
                  ? "Je profiel bepaalt hoe we trainingen, voeding en herstel berekenen. Houd je gewicht en RHR up-to-date voor de meest accurate begeleiding."
                  : "Your profile drives how we calculate training, nutrition and recovery. Keep weight and RHR current for the most accurate guidance."}
              </div>
            </div>
          </div>
        </Card>

        {/* Two-column body */}
        <div data-grid-collapse style={{ display: "grid", gridTemplateColumns: "1.4fr 1fr", gap: D.gap, alignItems: "flex-start" }}>

          {/* LEFT */}
          <div style={{ display: "flex", flexDirection: "column", gap: D.gap }}>

            {/* Vitals grid */}
            <Card padding={D.pad}>
              <SectionHead
                title={t("you")}
                subtitle={isNL ? "Basisstats voor alle berekeningen" : "Base stats for all calculations"}
                action={<Button variant="ghost" size="sm" icon="settings">{t("edit")}</Button>}
              />
              <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 14 }}>
                {vitals.map(([label, val, unit]) => (
                  <div key={label} style={{ padding: 14, borderRadius: "var(--ph-r-md)", background: "var(--ph-surface-muted)" }}>
                    <div className="ph-caption">{label.toUpperCase()}</div>
                    <div className="ph-mono" style={{ fontSize: 22, fontWeight: 600, marginTop: 6 }}>
                      {val}<span style={{ fontSize: 12, color: "var(--ph-text-muted)", fontWeight: 400, marginLeft: 4 }}>{unit}</span>
                    </div>
                  </div>
                ))}
              </div>
            </Card>

            {/* Devices */}
            <Card padding={D.pad}>
              <SectionHead
                title={t("devices")}
                subtitle={isNL ? "Bronnen voor je metingen" : "Sources for your measurements"}
                action={<Button variant="outline" size="sm" icon="plus">{isNL ? "Verbind" : "Connect"}</Button>}
              />
              <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
                {devices.map((d, i) => (
                  <div key={i} style={{
                    display: "flex", alignItems: "center", gap: 14,
                    padding: 14, borderRadius: "var(--ph-r-md)",
                    background: d.connected ? "var(--ph-surface-muted)" : "transparent",
                    border: d.connected ? "1px solid transparent" : "1px dashed var(--ph-border)",
                  }}>
                    <div style={{
                      width: 44, height: 44, borderRadius: 12, background: d.brand,
                      color: "#fff", display: "flex", alignItems: "center", justifyContent: "center",
                      fontWeight: 700, fontSize: 13, flexShrink: 0,
                      opacity: d.connected ? 1 : 0.4,
                    }}>{d.name.split(" ").map(w => w[0]).join("").slice(0, 2)}</div>
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{ fontSize: 14, fontWeight: 600 }}>{d.name}</div>
                      <div className="ph-caption">{d.types}</div>
                    </div>
                    <div style={{ textAlign: "right" }}>
                      {d.connected ? (
                        <>
                          <Tag tone="success" icon="check">{t("connected")}</Tag>
                          <div className="ph-caption" style={{ marginTop: 4 }}>{t("lastSync")} · {d.last}</div>
                        </>
                      ) : (
                        <Button size="sm" variant="outline">{isNL ? "Verbind" : "Link"}</Button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </Card>

            {/* Goals */}
            <Card padding={D.pad}>
              <SectionHead
                title={t("goals")}
                subtitle={isNL ? "Wat je wilt bereiken — bepaalt je plan" : "What you want — drives your plan"}
                action={<Button variant="ghost" size="sm" icon="plus" />}
              />
              <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                {goals.map((g, i) => (
                  <div key={i} style={{
                    display: "flex", alignItems: "center", gap: 12,
                    padding: 14, borderRadius: "var(--ph-r-md)",
                    background: g.primary ? "var(--ph-primary-soft)" : "var(--ph-surface-muted)",
                    border: g.primary ? "1px solid var(--ph-primary)" : "1px solid transparent",
                  }}>
                    <div style={{
                      width: 36, height: 36, borderRadius: 10,
                      background: g.primary ? "var(--ph-primary)" : "var(--ph-surface)",
                      color: g.primary ? "#fff" : "var(--ph-primary)",
                      display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0,
                    }}>
                      <Icon name={g.icon} size={16} />
                    </div>
                    <div style={{ flex: 1, fontSize: 14, fontWeight: 500 }}>{g.t}</div>
                    {g.primary && <Tag tone="primary">{isNL ? "Hoofd" : "Primary"}</Tag>}
                    <IconButton icon="settings" label="settings" />
                  </div>
                ))}
              </div>
            </Card>
          </div>

          {/* RIGHT */}
          <div style={{ display: "flex", flexDirection: "column", gap: D.gap }}>

            {/* Activity / availability */}
            <Card padding={D.pad}>
              <div className="ph-label">{t("activity")}</div>
              <div className="ph-h3" style={{ marginTop: 4 }}>{isNL ? "Regelmatig" : "Regular"}</div>
              <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 4 }}>{isNL ? "3–4× per week, gericht trainen" : "3–4× per week, training with intent"}</div>
              <div style={{ height: 1, background: "var(--ph-divider)", margin: "14px 0" }} />
              <div className="ph-label">{t("availability")}</div>
              <div style={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: 4, marginTop: 8 }}>
                {(isNL ? ["M","D","W","D","V","Z","Z"] : ["M","T","W","T","F","S","S"]).map((d, i) => {
                  const on = [0,1,3,4,5].includes(i);
                  return (
                    <div key={i} style={{
                      textAlign: "center", padding: "8px 0", borderRadius: 8,
                      background: on ? "var(--ph-primary)" : "var(--ph-surface-muted)",
                      color: on ? "#fff" : "var(--ph-text-muted)",
                      fontSize: 12, fontWeight: 600,
                    }}>{d}</div>
                  );
                })}
              </div>
              <div className="ph-caption" style={{ marginTop: 10 }}>5 {t("daysWeek")} · 6 {t("hoursWeek")}</div>
            </Card>

            {/* Privacy */}
            <Card padding={D.pad}>
              <SectionHead title={t("privacy")} action={<Button variant="ghost" size="sm" icon="settings" />} />
              <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
                {privacy.map(p => (
                  <div key={p.key} style={{ display: "flex", alignItems: "center", gap: 10 }}>
                    <Icon name={
                      p.value === "onlyMe" ? "settings" : p.value === "friends" ? "user" : "users"
                    } size={14} color="var(--ph-text-muted)" />
                    <div style={{ flex: 1, fontSize: 13, fontWeight: 500 }}>{p.label}</div>
                    <Tag tone={p.value === "onlyMe" ? "neutral" : "primary"}>
                      {t(p.value)}
                    </Tag>
                  </div>
                ))}
              </div>
            </Card>

            {/* Prefs */}
            <Card padding={D.pad}>
              <SectionHead title={t("prefs")} />
              <PrefRow icon="settings" label={t("language")} value={isNL ? "Nederlands" : "English"} />
              <PrefRow icon="trending" label={t("units")} value={units === "metric" ? (isNL ? "Metrisch (kg, cm)" : "Metric (kg, cm)") : "Imperial (lb, in)"} />
              <PrefRow icon="bell" label={isNL ? "Notificaties" : "Notifications"} value={isNL ? "Aan · stilte 22:00–07:00" : "On · quiet 22:00–07:00"} />
              <PrefRow icon="moon" label={isNL ? "Donkere modus" : "Dark mode"} value={isNL ? "Volgt systeem" : "Follow system"} />
            </Card>

            {/* Data export */}
            <Card padding={D.pad}>
              <div className="ph-label">{isNL ? "JOUW DATA" : "YOUR DATA"}</div>
              <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 6, marginBottom: 12 }}>
                {isNL
                  ? "Volledige export in CSV/JSON. Verwijdering binnen 30 dagen."
                  : "Full export in CSV/JSON. Deletion within 30 days."}
              </div>
              <Button variant="outline" full size="sm" icon="trending">{t("download")}</Button>
              <Button variant="ghost" full size="sm" style={{ marginTop: 6, color: "var(--ph-danger, #b14a4a)" }}>{t("erase")}</Button>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};

const PrefRow = ({ icon, label, value }) => (
  <div style={{ display: "flex", alignItems: "center", gap: 12, padding: "10px 0", borderTop: "1px solid var(--ph-divider)" }}>
    <Icon name={icon} size={16} color="var(--ph-text-muted)" />
    <div style={{ flex: 1 }}>
      <div style={{ fontSize: 13, fontWeight: 500 }}>{label}</div>
      <div className="ph-caption" style={{ marginTop: 2 }}>{value}</div>
    </div>
    <Icon name="chevronRight" size={16} color="var(--ph-text-faint)" />
  </div>
);

/* ─────────────────────────────────────────────
   PLAN SCREEN
   Week of training + today's session detail + mesocycle progress.
   Top: recovery-adapt banner. Middle: 7-day grid. Right: meso/period view.
   ───────────────────────────────────────────── */
const PlanScreen = ({ lang = "nl", density = "comfortable" }) => {
  const t = ppT(lang);
  const D = ppDensity(density);
  const isNL = lang === "nl";

  // index of expanded day in the mobile/tablet list. Defaults to 'today'.
  const [openDay, setOpenDay] = useStatePP(4);

  // 7-day plan with sessions per day. "actual" = how it was logged so far.
  const days = [
    { d: isNL ? "Ma" : "Mon", date: 28, type: "strength", title: isNL ? "Push" : "Push", time: "07:00", dur: "60m", done: true, intensity: "moderate",
      summary: isNL ? "Borst & schouders · 16 sets · 12.4 t volume" : "Chest & shoulders · 16 sets · 12.4 t volume",
      details: isNL
        ? ["Bench press 4×5 @ 75kg", "Overhead press 3×6 @ 45kg", "Incline DB press 3×10 @ 22kg", "Lateral raise 3×12 @ 10kg", "Triceps pushdown 3×12"]
        : ["Bench press 4×5 @ 75kg", "Overhead press 3×6 @ 45kg", "Incline DB press 3×10 @ 22kg", "Lateral raise 3×12 @ 10kg", "Triceps pushdown 3×12"]
    },
    { d: isNL ? "Di" : "Tue", date: 29, type: "run", title: "Z2 run", time: "07:30", dur: "45m", done: true, intensity: "easy",
      summary: isNL ? "Rustige duurloop · 6.4 km · 138 bpm gem." : "Easy endurance · 6.4 km · 138 bpm avg.",
      details: isNL
        ? ["Doel: hartslag onder 142 bpm", "Verdeling: Z1 12% · Z2 84% · Z3 4%", "Tempo: 7'02 / km gemiddeld"]
        : ["Target: HR below 142 bpm", "Distribution: Z1 12% · Z2 84% · Z3 4%", "Pace: 7'02 / km average"]
    },
    { d: isNL ? "Wo" : "Wed", date: 30, type: "rest", title: isNL ? "Rust" : "Rest", done: true,
      summary: isNL ? "Volledige rustdag · alleen wandelen aanbevolen" : "Full rest day · light walking only",
      details: isNL
        ? ["8.420 stappen gelogd", "Slaap 7u 38m · HRV 71 ms", "Geen extra training"]
        : ["8,420 steps logged", "Sleep 7h 38m · HRV 71 ms", "No extra training"]
    },
    { d: isNL ? "Do" : "Thu", date: 1, type: "strength", title: "Pull", time: "07:00", dur: "60m", done: true, intensity: "hard",
      summary: isNL ? "Rug & biceps · 18 sets · 13.1 t volume" : "Back & biceps · 18 sets · 13.1 t volume",
      details: isNL
        ? ["Deadlift 4×5 @ 110kg", "Pull-up 4×8 bw", "Barbell row 3×8 @ 65kg", "Face pull 3×12", "Hammer curl 3×10 @ 14kg"]
        : ["Deadlift 4×5 @ 110kg", "Pull-up 4×8 bw", "Barbell row 3×8 @ 65kg", "Face pull 3×12", "Hammer curl 3×10 @ 14kg"]
    },
    { d: isNL ? "Vr" : "Fri", date: 2, type: "strength", title: "Push", time: "07:00", dur: "60m", today: true, intensity: "moderate",
      summary: isNL ? "Borst & schouders · 16 sets gepland · ~12.4 t" : "Chest & shoulders · 16 sets planned · ~12.4 t",
      details: isNL
        ? ["Bench press 4×5 @ 75kg — PR poging op set 4", "Overhead press 3×6 @ 45kg", "Incline DB press 3×10 @ 22kg", "Lateral raise 3×12 — strict tempo", "Triceps pushdown 3×12 — RIR 2"]
        : ["Bench press 4×5 @ 75kg — PR attempt on set 4", "Overhead press 3×6 @ 45kg", "Incline DB press 3×10 @ 22kg", "Lateral raise 3×12 — strict tempo", "Triceps pushdown 3×12 — RIR 2"]
    },
    { d: isNL ? "Za" : "Sat", date: 3, type: "ride", title: "Long ride", time: "09:00", dur: "1h 40m", intensity: "moderate",
      summary: isNL ? "Lange duurrit · 45 km · gemengd Z2/Z3" : "Long endurance ride · 45 km · mixed Z2/Z3",
      details: isNL
        ? ["Route: heuvels rondom — 480m hoogtemeters", "Doel: 80% in Z2, eindblok 15 min Z3", "Voeding: 60g koolhydraten/uur"]
        : ["Route: rolling hills — 480m elevation", "Target: 80% in Z2, final block 15 min Z3", "Fuel: 60g carbs/hr"]
    },
    { d: isNL ? "Zo" : "Sun", date: 4, type: "mobility", title: isNL ? "Mobility" : "Mobility", time: "10:00", dur: "20m", intensity: "easy",
      summary: isNL ? "Mobiliteit & stretching · focus heupen" : "Mobility & stretching · hip focus",
      details: isNL
        ? ["90/90 hip switches 3×8", "World's greatest stretch 3×6", "Thoracic opener 3×30s", "Couch stretch 2×45s/zijde"]
        : ["90/90 hip switches 3×8", "World's greatest stretch 3×6", "Thoracic opener 3×30s", "Couch stretch 2×45s/side"]
    },
  ];

  const dayColor = {
    strength: "var(--ph-primary)",
    run: "var(--ph-data-3)",
    ride: "var(--ph-data-4)",
    mobility: "var(--ph-data-2)",
    rest: "var(--ph-text-faint)",
  };
  const dayIcon = {
    strength: "dumbbell", run: "footprints", ride: "bike",
    mobility: "leaf", rest: "moon",
  };

  // Mesocycle: 4-week build + 1 deload.
  const meso = {
    name: isNL ? "Hypertrofie blok 2" : "Hypertrophy block 2",
    week: 3, weeks: 5, deloadAt: 5,
  };

  // Today's session detail (Friday Push)
  const session = {
    title: isNL ? "Push · borst & schouders" : "Push · chest & shoulders",
    blocks: [
      { name: "Bench press", sets: "4×5", load: "75 kg", note: isNL ? "PR poging op set 4" : "PR attempt on set 4" },
      { name: "Overhead press", sets: "3×6", load: "45 kg", note: isNL ? "Pauze 2:00" : "Rest 2:00" },
      { name: "Incline DB press", sets: "3×10", load: "22 kg", note: "" },
      { name: "Lateral raise", sets: "3×12", load: "10 kg", note: isNL ? "Strict tempo" : "Strict tempo" },
      { name: "Triceps pushdown", sets: "3×12", load: "—", note: isNL ? "RIR 2" : "RIR 2" },
    ],
    expected: { volume: "12.4 t", sets: 16, dur: "60 min", rpe: 7 },
  };

  return (
    <div style={{ height: "100%", overflow: "auto", background: "var(--ph-bg)" }}>
      <div style={{ padding: D.pad, maxWidth: 1180, margin: "0 auto", display: "flex", flexDirection: "column", gap: D.vgap }}>

        {/* Header */}
        <div data-mobile-stack style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", gap: 14 }}>
          <div>
            <div className="ph-caption">{isNL ? "Vrijdag · 1 mei" : "Friday · May 1"}</div>
            <div className="ph-h1" style={{ marginTop: 4 }}>{t("plan")}</div>
            <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 4 }}>
              {meso.name} · {t("week")} {meso.week}/{meso.weeks}
            </div>
          </div>
          <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
            <Button variant="ghost" icon="settings">{isNL ? "Pas plan aan" : "Tweak plan"}</Button>
            <Button variant="secondary" icon="plus">{isNL ? "Sessie toevoegen" : "Add session"}</Button>
          </div>
        </div>

        {/* Recovery adapt banner */}
        <Card padding={D.pad} style={{ background: "linear-gradient(135deg, var(--ph-primary-soft) 0%, var(--ph-surface) 60%)" }}>
          <div data-mobile-stack style={{ display: "flex", alignItems: "center", gap: 18 }}>
            <div style={{
              width: 48, height: 48, borderRadius: 14,
              background: "var(--ph-primary)", color: "#fff",
              display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0,
            }}>
              <Icon name="sparkles" size={20} />
            </div>
            <div style={{ flex: 1, minWidth: 240 }}>
              <div className="ph-label" style={{ color: "var(--ph-primary)" }}>{t("autoTune").toUpperCase()}</div>
              <div className="ph-h3" style={{ marginTop: 4 }}>
                {isNL ? "Goed hersteld — push gehouden zoals gepland" : "Recovered — push kept as planned"}
              </div>
              <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 6, maxWidth: 540 }}>
                {isNL
                  ? "HRV 68 ms (+4) · slaap 7u 42m. Geen schaling nodig. Op woensdag was load hoog → cardio teruggebracht naar 30 min Z2."
                  : "HRV 68 ms (+4) · sleep 7h 42m. No scaling needed. Wednesday was high-load → Z2 cardio reduced to 30 min."}
              </div>
            </div>
            <Button variant="outline" size="sm">{t("details")}</Button>
          </div>
        </Card>

        {/* Two-pane: week grid + meso/today detail */}
        <div data-grid-collapse style={{ display: "grid", gridTemplateColumns: "1.6fr 1fr", gap: D.gap, alignItems: "flex-start" }}>

          {/* LEFT — Week + today detail */}
          <div style={{ display: "flex", flexDirection: "column", gap: D.gap }}>

            {/* Week — desktop grid / mobile+tablet list with expandable rows */}
            <Card padding={D.pad}>
              <SectionHead
                title={t("thisWeek")}
                subtitle={isNL ? "5 sessies · 6 uur gepland" : "5 sessions · 6 hours planned"}
                action={<Segmented value="week" onChange={() => {}} options={[
                  { value: "week", label: t("thisWeek") },
                  { value: "next", label: isNL ? "Volgende" : "Next" },
                ]} />}
              />

              {/* GRID — desktop only */}
              <div className="ph-week-grid" style={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: 8 }}>
                {days.map((day, i) => {
                  const c = dayColor[day.type];
                  const ic = dayIcon[day.type];
                  const isRest = day.type === "rest";
                  return (
                    <div key={i} style={{
                      borderRadius: "var(--ph-r-md)", padding: 12, minHeight: 150,
                      background: day.today ? "var(--ph-primary-soft)" : "var(--ph-surface-muted)",
                      border: day.today ? "2px solid var(--ph-primary)" : "1px solid transparent",
                      display: "flex", flexDirection: "column", gap: 6,
                      position: "relative", overflow: "hidden",
                    }}>
                      <div style={{ display: "flex", alignItems: "baseline", justifyContent: "space-between" }}>
                        <span className="ph-caption">{day.d}</span>
                        <span className="ph-mono" style={{ fontSize: 13, fontWeight: 600 }}>{day.date}</span>
                      </div>
                      {!isRest ? (
                        <>
                          <div style={{
                            width: 28, height: 28, borderRadius: 8,
                            background: day.done ? "var(--ph-surface)" : c,
                            color: day.done ? c : "#fff",
                            display: "flex", alignItems: "center", justifyContent: "center",
                            border: day.done ? `1px solid ${c}` : "none",
                            marginTop: 4,
                          }}>
                            <Icon name={ic} size={14} />
                          </div>
                          <div style={{ fontSize: 13, fontWeight: 600, marginTop: 4 }}>{day.title}</div>
                          <div className="ph-caption" style={{ fontSize: 11 }}>{day.time} · {day.dur}</div>
                          {day.done && (
                            <div style={{ marginTop: "auto", display: "flex", alignItems: "center", gap: 4, fontSize: 11, color: "var(--ph-success, #2f6f4e)", fontWeight: 600 }}>
                              <Icon name="check" size={11} strokeWidth={3} />
                              {isNL ? "Klaar" : "Done"}
                            </div>
                          )}
                          {day.today && (
                            <div style={{ marginTop: "auto", fontSize: 11, color: "var(--ph-primary)", fontWeight: 700 }}>
                              {isNL ? "Vandaag" : "Today"}
                            </div>
                          )}
                        </>
                      ) : (
                        <div style={{ display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", flex: 1, gap: 4 }}>
                          <Icon name="moon" size={18} color="var(--ph-text-faint)" />
                          <div className="ph-caption">{day.title}</div>
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>

              {/* LIST — mobile + tablet, native <details> for expand */}
              <div className="ph-week-list" style={{ display: "none", flexDirection: "column", gap: 6 }}>
                {days.map((day, i) => {
                  const c = dayColor[day.type];
                  const ic = dayIcon[day.type];
                  const isRest = day.type === "rest";
                  const isOpen = openDay === i;
                  return (
                    <div key={i}
                      onClick={() => setOpenDay(isOpen ? -1 : i)}
                      style={{
                      borderRadius: "var(--ph-r-md)",
                      cursor: "pointer",
                      background: isOpen ? "var(--ph-primary-soft)" : (day.today ? "var(--ph-primary-soft)" : "var(--ph-surface-muted)"),
                      border: isOpen ? "2px solid var(--ph-primary)" : (day.today ? "2px solid var(--ph-primary)" : "1px solid transparent"),
                      overflow: "hidden",
                      transition: "background .15s",
                    }}>
                      <div style={{
                        display: "grid", gridTemplateColumns: "44px 36px 1fr auto",
                        gap: 12, alignItems: "center", padding: "12px 14px",
                      }}>
                        <div style={{ display: "flex", flexDirection: "column", lineHeight: 1.1 }}>
                          <span className="ph-caption" style={{ fontSize: 10 }}>{day.d}</span>
                          <span className="ph-mono" style={{ fontSize: 17, fontWeight: 700, color: (day.today || isOpen) ? "var(--ph-primary)" : "var(--ph-text)" }}>{day.date}</span>
                        </div>
                        <div style={{
                          width: 36, height: 36, borderRadius: 10,
                          background: isRest ? "transparent" : (day.done ? "var(--ph-surface)" : c),
                          color: isRest ? "var(--ph-text-faint)" : (day.done ? c : "#fff"),
                          display: "flex", alignItems: "center", justifyContent: "center",
                          border: isRest ? "1px dashed var(--ph-divider)" : (day.done ? `1px solid ${c}` : "none"),
                        }}>
                          <Icon name={ic} size={16} />
                        </div>
                        <div style={{ minWidth: 0 }}>
                          <div style={{ fontSize: 14, fontWeight: 600, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>{day.title}</div>
                          {!isRest && <div className="ph-caption" style={{ fontSize: 11 }}>{day.time} · {day.dur}</div>}
                        </div>
                        {day.done ? (
                          <div style={{
                            width: 26, height: 26, borderRadius: "50%",
                            background: "var(--ph-success, #2f6f4e)", color: "#fff",
                            display: "flex", alignItems: "center", justifyContent: "center",
                          }}>
                            <Icon name="check" size={14} strokeWidth={3} />
                          </div>
                        ) : day.today ? (
                          <Tag tone="primary">{isNL ? "Vandaag" : "Today"}</Tag>
                        ) : (
                          <div style={{ width: 26, height: 26, borderRadius: "50%", border: "1.5px dashed var(--ph-divider)" }} />
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>

              {/* Detail card — full width, mobile/tablet only, shown below list */}
              {(() => {
                const sel = openDay >= 0 ? days[openDay] : null;
                if (!sel) return null;
                const c = dayColor[sel.type];
                const ic = dayIcon[sel.type];
                const isRest = sel.type === "rest";
                return (
                  <div className="ph-week-detail" style={{ display: "none", marginTop: 12 }}>
                    <div style={{
                      borderRadius: "var(--ph-r-md)",
                      background: "var(--ph-surface)",
                      border: "1px solid var(--ph-divider)",
                      overflow: "hidden",
                    }}>
                      {/* header strip with day type colour */}
                      <div style={{
                        display: "flex", alignItems: "center", gap: 12,
                        padding: "14px 16px",
                        background: isRest ? "var(--ph-surface-muted)" : `color-mix(in oklab, ${c} 12%, var(--ph-surface))`,
                        borderBottom: "1px solid var(--ph-divider)",
                      }}>
                        <div style={{
                          width: 40, height: 40, borderRadius: 10,
                          background: isRest ? "var(--ph-surface)" : c,
                          color: isRest ? "var(--ph-text-faint)" : "#fff",
                          display: "flex", alignItems: "center", justifyContent: "center",
                          flexShrink: 0,
                        }}>
                          <Icon name={ic} size={18} />
                        </div>
                        <div style={{ flex: 1, minWidth: 0 }}>
                          <div style={{ fontSize: 15, fontWeight: 600 }}>{sel.title}</div>
                          <div className="ph-caption" style={{ marginTop: 2 }}>
                            {sel.d} {sel.date} {!isRest && sel.time ? `· ${sel.time} · ${sel.dur}` : ""}
                          </div>
                        </div>
                        {sel.done && <Tag tone="success" icon="check">{isNL ? "Klaar" : "Done"}</Tag>}
                        {sel.today && <Tag tone="primary">{isNL ? "Vandaag" : "Today"}</Tag>}
                      </div>

                      {/* body */}
                      <div style={{ padding: "14px 16px" }}>
                        <div className="ph-body-sm" style={{ color: "var(--ph-text)", marginBottom: 12, lineHeight: 1.5 }}>
                          {sel.summary}
                        </div>
                        <div className="ph-label" style={{ marginBottom: 8 }}>
                          {isRest
                            ? (isNL ? "DAG OVERZICHT" : "DAY OVERVIEW")
                            : sel.done
                              ? (isNL ? "WAT JE DEED" : "WHAT YOU DID")
                              : (isNL ? "WAT JE GAAT DOEN" : "WHAT'S PLANNED")}
                        </div>
                        <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                          {sel.details && sel.details.map((line, j) => (
                            <div key={j} style={{
                              display: "flex", gap: 10, alignItems: "flex-start",
                              padding: "10px 12px", borderRadius: "var(--ph-r-sm)",
                              background: "var(--ph-surface-muted)",
                              fontSize: 13, lineHeight: 1.45,
                            }}>
                              <div style={{
                                width: 20, height: 20, borderRadius: 6,
                                background: isRest ? "var(--ph-text-faint)" : c,
                                color: "#fff", flexShrink: 0,
                                display: "flex", alignItems: "center", justifyContent: "center",
                                fontSize: 11, fontWeight: 700,
                              }}>{j + 1}</div>
                              <div style={{ flex: 1 }}>{line}</div>
                            </div>
                          ))}
                        </div>

                        {/* actions */}
                        <div style={{ display: "flex", gap: 8, marginTop: 14, flexWrap: "wrap" }}>
                          {sel.done ? (
                            <Button size="sm" variant="ghost" icon="trending">{isNL ? "Bekijk resultaat" : "View result"}</Button>
                          ) : sel.today ? (
                            <>
                              <Button size="sm" icon={ic}>{isNL ? "Start sessie" : "Start session"}</Button>
                              <Button size="sm" variant="ghost">{t("swap")}</Button>
                            </>
                          ) : !isRest ? (
                            <>
                              <Button size="sm" variant="outline">{t("move")}</Button>
                              <Button size="sm" variant="ghost">{t("swap")}</Button>
                              <Button size="sm" variant="ghost">{t("details")}</Button>
                            </>
                          ) : (
                            <Button size="sm" variant="ghost" icon="moon">{isNL ? "Herstel-tips" : "Recovery tips"}</Button>
                          )}
                        </div>
                      </div>
                    </div>
                  </div>
                );
              })()}

              <style>{`
                @media (max-width: 900px) {
                  .ph-week-grid { display: none !important; }
                  .ph-week-list { display: flex !important; }
                  .ph-week-detail { display: block !important; }
                }
              `}</style>

              {/* week load bar */}
              <div style={{ marginTop: 16 }}>
                <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 6 }}>
                  <span className="ph-label">{t("weekLoad").toUpperCase()}</span>
                  <span className="ph-mono" style={{ fontSize: 12, color: "var(--ph-text-muted)" }}>
                    62 / 80 TSS
                  </span>
                </div>
                <div style={{ height: 8, background: "var(--ph-surface-muted)", borderRadius: 999, overflow: "hidden", position: "relative" }}>
                  <div style={{
                    height: "100%", width: `${(62/80)*100}%`,
                    background: "linear-gradient(90deg, var(--ph-data-2), var(--ph-primary))",
                    borderRadius: 999,
                  }} />
                  <div style={{
                    position: "absolute", left: "75%", top: -4, bottom: -4,
                    width: 2, background: "var(--ph-warning, #c7873a)", borderRadius: 1,
                  }} />
                </div>
                <div className="ph-caption" style={{ marginTop: 6 }}>
                  {isNL ? "Drempel bij 60 TSS — verder is hoog." : "Threshold at 60 TSS — beyond is high."}
                </div>
              </div>
            </Card>

            {/* Today session detail */}
            <Card padding={D.pad}>
              <SectionHead
                title={t("today")}
                subtitle={session.title}
                action={<Button icon="dumbbell">{isNL ? "Start" : "Start"}</Button>}
              />
              <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: 10, marginBottom: 14 }}>
                {[
                  ["VOL", session.expected.volume],
                  ["SETS", session.expected.sets],
                  ["DUR", session.expected.dur],
                  ["RPE", session.expected.rpe],
                ].map(([l, v]) => (
                  <div key={l} style={{ padding: 10, borderRadius: "var(--ph-r-sm)", background: "var(--ph-surface-muted)" }}>
                    <div className="ph-caption">{l}</div>
                    <div className="ph-mono" style={{ fontSize: 18, fontWeight: 600, marginTop: 4 }}>{v}</div>
                  </div>
                ))}
              </div>
              <div style={{ display: "flex", flexDirection: "column" }}>
                {session.blocks.map((b, i) => (
                  <div key={i} style={{
                    display: "grid", gridTemplateColumns: "32px 1fr auto auto auto",
                    gap: 12, alignItems: "center",
                    padding: "12px 0", borderTop: i === 0 ? "none" : "1px solid var(--ph-divider)",
                  }}>
                    <div style={{
                      width: 30, height: 30, borderRadius: 8,
                      background: "var(--ph-primary-soft)", color: "var(--ph-primary)",
                      display: "flex", alignItems: "center", justifyContent: "center",
                      fontSize: 12, fontWeight: 700,
                    }}>{i + 1}</div>
                    <div>
                      <div style={{ fontSize: 14, fontWeight: 600 }}>{b.name}</div>
                      {b.note && <div className="ph-caption" style={{ marginTop: 2 }}>{b.note}</div>}
                    </div>
                    <Tag>{b.sets}</Tag>
                    <span className="ph-mono" style={{ fontSize: 13, fontWeight: 600, color: "var(--ph-text-muted)", minWidth: 60, textAlign: "right" }}>{b.load}</span>
                    <IconButton icon="settings" label="swap" />
                  </div>
                ))}
              </div>
            </Card>
          </div>

          {/* RIGHT — Meso + adapt panel + how-you-feel */}
          <div data-unstick-mobile style={{ display: "flex", flexDirection: "column", gap: D.gap, position: "sticky", top: 0 }}>

            {/* Mesocycle progress */}
            <Card padding={D.pad}>
              <SectionHead
                title={t("mesocycle")}
                subtitle={meso.name}
                action={<Tag tone="primary">{t("phase")}: {t("build")}</Tag>}
              />
              <div style={{ display: "grid", gridTemplateColumns: `repeat(${meso.weeks}, 1fr)`, gap: 6, marginBottom: 12 }}>
                {Array.from({ length: meso.weeks }).map((_, i) => {
                  const w = i + 1;
                  const isDeload = w === meso.deloadAt;
                  const past = w < meso.week;
                  const cur = w === meso.week;
                  return (
                    <div key={i} style={{
                      padding: "10px 6px", borderRadius: "var(--ph-r-sm)",
                      background: cur ? "var(--ph-primary)" : past ? "var(--ph-primary-soft)" : isDeload ? "var(--ph-warning-soft, #f4ead8)" : "var(--ph-surface-muted)",
                      color: cur ? "#fff" : past ? "var(--ph-primary)" : "var(--ph-text-muted)",
                      textAlign: "center",
                      border: cur ? "none" : isDeload ? "1px dashed var(--ph-warning, #c7873a)" : "none",
                    }}>
                      <div style={{ fontSize: 11, fontWeight: 500 }}>W{w}</div>
                      <div style={{ fontSize: 13, fontWeight: 700, marginTop: 2 }}>
                        {isDeload ? "↓" : ["60","70","80","90","↓"][i] || "—"}
                      </div>
                      {isDeload && <div style={{ fontSize: 9, marginTop: 2, fontWeight: 600 }}>{t("deload").toUpperCase()}</div>}
                    </div>
                  );
                })}
              </div>
              <div className="ph-caption" style={{ lineHeight: 1.5 }}>
                {isNL
                  ? "Volume bouwt op tot week 4, dan deload. Gewicht stijgt 2.5kg/week op compound lifts."
                  : "Volume builds through week 4, then deload. Loads add 2.5kg/wk on compounds."}
              </div>
            </Card>

            {/* How you feel input (drives adaptation) */}
            <Card padding={D.pad}>
              <div className="ph-label">{isNL ? "VANDAAG" : "TODAY"}</div>
              <div className="ph-h3" style={{ marginTop: 4 }}>{t("feel")}</div>
              <div style={{ display: "flex", gap: 6, marginTop: 12 }}>
                {[1,2,3,4,5].map(n => {
                  const sel = n === 4;
                  return (
                    <button key={n} style={{
                      all: "unset", cursor: "pointer", flex: 1,
                      padding: "12px 0", textAlign: "center",
                      borderRadius: "var(--ph-r-sm)",
                      background: sel ? "var(--ph-primary)" : "var(--ph-surface-muted)",
                      color: sel ? "#fff" : "var(--ph-text)",
                      fontSize: 18, fontWeight: 600,
                    }}>{["😩","🙁","😐","🙂","😎"][n-1]}</button>
                  );
                })}
              </div>
              <div className="ph-caption" style={{ marginTop: 10, lineHeight: 1.5 }}>
                {isNL
                  ? "Voel je vandaag goed (4/5) — plan blijft staan. Bij 1–2 schalen we automatisch terug."
                  : "Feeling good today (4/5) — plan stays. At 1–2 we scale back automatically."}
              </div>
            </Card>

            {/* Volume vs target */}
            <Card padding={D.pad}>
              <div className="ph-label">{t("targetVolume").toUpperCase()}</div>
              <div style={{ marginTop: 12, display: "flex", flexDirection: "column", gap: 12 }}>
                {[
                  [isNL ? "Borst" : "Chest", 14, 18, "var(--ph-data-1)"],
                  [isNL ? "Rug" : "Back", 16, 20, "var(--ph-data-3)"],
                  [isNL ? "Benen" : "Legs", 12, 16, "var(--ph-data-4)"],
                  [isNL ? "Schouders" : "Shoulders", 10, 12, "var(--ph-data-5)"],
                  [isNL ? "Armen" : "Arms", 8, 10, "var(--ph-data-2)"],
                ].map(([name, v, max, col]) => (
                  <div key={name}>
                    <div style={{ display: "flex", justifyContent: "space-between", fontSize: 12, marginBottom: 4 }}>
                      <span>{name}</span>
                      <span className="ph-mono" style={{ color: "var(--ph-text-muted)" }}>{v}<span style={{ color: "var(--ph-text-faint)" }}> / {max}</span></span>
                    </div>
                    <div style={{ height: 6, background: "var(--ph-surface-muted)", borderRadius: 999, overflow: "hidden" }}>
                      <div style={{ height: "100%", width: `${(v/max)*100}%`, background: col, borderRadius: 999 }} />
                    </div>
                  </div>
                ))}
              </div>
              <div className="ph-caption" style={{ marginTop: 10 }}>
                {isNL ? "Sets per spiergroep · doel deze week" : "Working sets per group · target this week"}
              </div>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};

/* ─────────────────────────────────────────────
   ONBOARDING FLOW — stitches the 9 steps
   ───────────────────────────────────────────── */
const OnboardingFlow = ({ lang = "nl", initial = 1 }) => {
  const [step, setStep] = useStatePP(initial);
  const [data, setData] = useStatePP({
    goals: ["longevity", "strength"],
    about: { gender: "m", age: "34", height: "178", weight: "74.5" },
    activity: "regular",
    avail: { days: [0,1,3,4,5], hours: 6 },
    devices: ["apple_watch", "scale"],
    diet: { style: "high_protein", restrictions: ["Lactose"] },
    baseline: { rhr: "52", hrv: "68" },
  });
  const set = (k, v) => setData(d => ({ ...d, [k]: v }));
  const next = () => setStep(s => Math.min(9, s + 1));
  const back = () => setStep(s => Math.max(1, s - 1));

  const props = { lang, onNext: next, onBack: back };

  return (
    <div style={{ width: "100%", height: "100%", background: "var(--ph-bg)", overflow: "hidden" }}>
      {step === 1 && <OnbWelcome onNext={next} onSkip={() => setStep(2)} lang={lang} />}
      {step === 2 && <OnbGoals value={data.goals} onChange={v => set("goals", v)} {...props} />}
      {step === 3 && <OnbAbout value={data.about} onChange={v => set("about", v)} {...props} />}
      {step === 4 && <OnbActivity value={data.activity} onChange={v => set("activity", v)} {...props} />}
      {step === 5 && <OnbAvailability value={data.avail} onChange={v => set("avail", v)} {...props} />}
      {step === 6 && <OnbDevices value={data.devices} onChange={v => set("devices", v)} onSkip={next} {...props} />}
      {step === 7 && <OnbDiet value={data.diet} onChange={v => set("diet", v)} {...props} />}
      {step === 8 && <OnbBaseline value={data.baseline} onChange={v => set("baseline", v)} onSkip={next} {...props} />}
      {step === 9 && <OnbDone data={data} onFinish={() => setStep(1)} lang={lang} />}
    </div>
  );
};

Object.assign(window, {
  ProfileScreen, PlanScreen, OnboardingFlow, PrefRow,
});
