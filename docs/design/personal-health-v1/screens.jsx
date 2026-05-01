/* Personal Health — additional screens.
   Track (Strength), Track (Food + supplements), Tablet rail, Foldable.
   Reads i18n + units from props; uses primitives on window.* */

const { useState: useStateS } = React;

/* ───────────── i18n ───────────── */
const STRINGS = {
  nl: {
    today: "Vandaag", track: "Loggen", plan: "Plan", progress: "Voortgang", community: "Community", profile: "Profiel",
    strength: "Krachttraining", food: "Voeding",
    foodSub: "Eerst eten — supplementen vullen alleen wat ontbreekt",
    micros: "Micronutriënten", microsSub: "Uit voeding · supplement vult tekort",
    fromFood: "uit voeding", fromSupp: "aanvulling", gap: "tekort",
    pushDay: "Push dag · borst & schouders",
    setsLogged: "sets gelogd", volume: "volume", duration: "duur",
    addExercise: "Oefening toevoegen", finishSession: "Sessie afronden",
    rest: "Rust", restNow: "Rust nu", lastSet: "Vorige set", target: "Doel",
    weight: "kg", reps: "herh", rir: "RIR",
    breakfast: "Ontbijt", lunch: "Lunch", dinner: "Diner", snacks: "Snacks",
    supplements: "Aanvullingen", supplementsSub: "Alleen wat voeding vandaag mist",
    addMeal: "Maaltijd toevoegen", addSupplement: "Aanvulling",
    protein: "Eiwit", carbs: "Koolh", fat: "Vet", fiber: "Vezel", kcal: "kcal",
    remaining: "resterend", goal: "doel", logged: "gelogd",
    today_date: "Vrijdag · 1 mei",
    quickAdd: "Snel toevoegen", barcode: "Barcode", recent: "Recent", search: "Zoek voedsel of merk",
    morning: "Ochtend", afternoon: "Middag", evening: "Avond",
  },
  en: {
    today: "Today", track: "Track", plan: "Plan", progress: "Progress", community: "Community", profile: "Profile",
    strength: "Strength training", food: "Nutrition",
    foodSub: "Food first — supplements only fill what's missing",
    micros: "Micronutrients", microsSub: "From food · supplements fill the gap",
    fromFood: "from food", fromSupp: "top-up", gap: "gap",
    pushDay: "Push day · chest & shoulders",
    setsLogged: "sets logged", volume: "volume", duration: "duration",
    addExercise: "Add exercise", finishSession: "Finish session",
    rest: "Rest", restNow: "Resting now", lastSet: "Last set", target: "Target",
    weight: "kg", reps: "reps", rir: "RIR",
    breakfast: "Breakfast", lunch: "Lunch", dinner: "Dinner", snacks: "Snacks",
    supplements: "Top-ups", supplementsSub: "Only what food didn't cover today",
    addMeal: "Add meal", addSupplement: "Top-up",
    protein: "Protein", carbs: "Carbs", fat: "Fat", fiber: "Fiber", kcal: "kcal",
    remaining: "remaining", goal: "goal", logged: "logged",
    today_date: "Friday · May 1",
    quickAdd: "Quick add", barcode: "Barcode", recent: "Recent", search: "Search foods or brands",
    morning: "Morning", afternoon: "Afternoon", evening: "Evening",
  },
};
const useT = (lang = "nl") => (k) => (STRINGS[lang] && STRINGS[lang][k]) || STRINGS.en[k] || k;

/* ───────────── unit helpers ───────────── */
const lbsPerKg = 2.20462;
const fmtWeight = (kg, units) => units === "imperial"
  ? `${(kg * lbsPerKg).toFixed(1)} lb`
  : `${kg.toFixed(1)} kg`;
const weightUnit = (units) => units === "imperial" ? "lb" : "kg";

/* ───────────── density tokens ───────────── */
const densityScale = (d) => ({
  compact:     { gap: 10, pad: 14, vgap: 12 },
  comfortable: { gap: 16, pad: 20, vgap: 18 },
  spacious:    { gap: 22, pad: 26, vgap: 26 },
}[d] || { gap: 16, pad: 20, vgap: 18 });

/* ───────────── Strength session screen ───────────── */
const StrengthScreen = ({ lang = "nl", units = "metric", density = "comfortable" }) => {
  const t = useT(lang);
  const [tab, setTab] = useStateS("session");
  const D = densityScale(density);

  const exercises = [
    {
      id: 1, name: "Bench press", group: "Borst",
      sets: [
        { w: 60, r: 8, rir: 3, done: true },
        { w: 70, r: 6, rir: 2, done: true },
        { w: 75, r: 5, rir: 1, done: true },
        { w: 75, r: 5, rir: 1, done: false, current: true },
      ],
      target: "4×5 @ 75kg",
    },
    {
      id: 2, name: "Overhead press", group: "Schouders",
      sets: [
        { w: 40, r: 8, rir: 3, done: true },
        { w: 45, r: 6, rir: 2, done: false },
        { w: 45, r: 6, rir: 2, done: false },
      ],
      target: "3×6 @ 45kg",
    },
    {
      id: 3, name: "Incline dumbbell", group: "Borst",
      sets: [{ w: 22, r: 10, rir: 2, done: false }, { w: 22, r: 10, rir: 2, done: false }, { w: 22, r: 10, rir: 2, done: false }],
      target: "3×10 @ 22kg",
    },
    {
      id: 4, name: "Lateral raise", group: "Schouders",
      sets: [{ w: 10, r: 12, rir: 2, done: false }, { w: 10, r: 12, rir: 2, done: false }, { w: 10, r: 12, rir: 2, done: false }],
      target: "3×12 @ 10kg",
    },
  ];

  const totalSets = exercises.reduce((a, e) => a + e.sets.length, 0);
  const doneSets = exercises.reduce((a, e) => a + e.sets.filter(s => s.done).length, 0);
  const volume = exercises.reduce((a, e) => a + e.sets.filter(s => s.done).reduce((b, s) => b + s.w * s.r, 0), 0);

  return (
    <div style={{ height: "100%", overflow: "auto", background: "var(--ph-bg)" }}>
      <div style={{ padding: D.pad, maxWidth: 1100, margin: "0 auto", display: "flex", flexDirection: "column", gap: D.vgap }}>

        {/* Header */}
        <div data-mobile-stack style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", gap: 14 }}>
          <div>
            <div className="ph-caption">{t("today_date")}</div>
            <div className="ph-h1" style={{ marginTop: 4 }}>{t("pushDay")}</div>
            <div style={{ display: "flex", gap: 8, marginTop: 10, flexWrap: "wrap" }}>
              <Tag tone="primary" icon="dumbbell">{doneSets}/{totalSets} {t("setsLogged")}</Tag>
              <Tag icon="flame">{fmtWeight(volume, units)} {t("volume")}</Tag>
              <Tag icon="activity">38 min {t("duration")}</Tag>
            </div>
          </div>
          <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
            <Button variant="ghost" icon="settings" />
            <Button variant="secondary">{t("addExercise")}</Button>
            <Button>{t("finishSession")}</Button>
          </div>
        </div>

        {/* Rest timer + last set */}
        <Card padding={D.pad} style={{ background: "linear-gradient(135deg, var(--ph-primary-soft), var(--ph-surface) 65%)" }}>
          <div style={{ display: "flex", alignItems: "center", gap: D.gap, flexWrap: "wrap" }}>
            <RingGauge value={62} max={120} size={120} stroke={10} label={t("rest")} sublabel="0:48" color="var(--ph-data-3)" />
            <div style={{ flex: 1, minWidth: 240 }}>
              <div className="ph-label">{t("lastSet")}</div>
              <div className="ph-h2" style={{ marginTop: 4 }}>Bench press · {fmtWeight(75, units)} × 5 · RIR 1</div>
              <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 6 }}>
                {t("target")}: {exercises[0].target} · volgende set in 12s
              </div>
            </div>
            <div style={{ display: "flex", gap: 8 }}>
              <Button variant="outline" size="sm">+30s</Button>
              <Button variant="outline" size="sm">Skip</Button>
            </div>
          </div>
        </Card>

        {/* Exercise cards */}
        <div style={{ display: "grid", gap: D.gap }}>
          {exercises.map(ex => (
            <Card key={ex.id} padding={D.pad}>
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 10 }}>
                <div>
                  <div className="ph-h3">{ex.name}</div>
                  <div className="ph-caption" style={{ marginTop: 2 }}>{ex.group} · {t("target")}: {ex.target}</div>
                </div>
                <div style={{ display: "flex", gap: 6 }}>
                  <IconButton icon="trending" label="history" />
                  <IconButton icon="settings" label="settings" />
                </div>
              </div>

              {/* Sets table */}
              <div style={{ display: "grid", gridTemplateColumns: "32px 1fr 1fr 1fr 80px 32px", gap: 8, alignItems: "center" }}>
                <div className="ph-caption">#</div>
                <div className="ph-caption">{weightUnit(units).toUpperCase()}</div>
                <div className="ph-caption">{t("reps").toUpperCase()}</div>
                <div className="ph-caption">{t("rir")}</div>
                <div className="ph-caption" style={{ textAlign: "center" }}>STATUS</div>
                <div />

                {ex.sets.map((s, i) => {
                  const isCurrent = s.current;
                  const bg = isCurrent ? "var(--ph-primary-soft)" : "transparent";
                  return (
                    <React.Fragment key={i}>
                      <div className="ph-mono" style={{
                        fontSize: 13, color: "var(--ph-text-muted)",
                        padding: "8px 6px", background: bg, borderRadius: 6
                      }}>{i + 1}</div>
                      <div style={{ background: bg, padding: "6px 8px", borderRadius: 6 }}>
                        <SetCell value={units === "imperial" ? (s.w * lbsPerKg).toFixed(1) : s.w} done={s.done} />
                      </div>
                      <div style={{ background: bg, padding: "6px 8px", borderRadius: 6 }}>
                        <SetCell value={s.r} done={s.done} />
                      </div>
                      <div style={{ background: bg, padding: "6px 8px", borderRadius: 6 }}>
                        <SetCell value={s.rir} done={s.done} muted />
                      </div>
                      <div style={{ display: "flex", justifyContent: "center", background: bg, padding: "6px 0", borderRadius: 6 }}>
                        {s.done && <Tag tone="success" icon="check">Done</Tag>}
                        {isCurrent && <Tag tone="primary">Now</Tag>}
                        {!s.done && !isCurrent && <Tag>—</Tag>}
                      </div>
                      <div style={{ display: "flex", justifyContent: "center", background: bg, padding: "6px 0", borderRadius: 6 }}>
                        {!s.done && <IconButton icon="check" label="log set" variant="filled" size={28} />}
                      </div>
                    </React.Fragment>
                  );
                })}
              </div>

              {/* Volume bar */}
              <div style={{ marginTop: 14, display: "flex", alignItems: "center", gap: 12 }}>
                <div style={{ flex: 1, height: 6, background: "var(--ph-surface-muted)", borderRadius: 999, overflow: "hidden" }}>
                  <div style={{
                    height: "100%",
                    width: `${(ex.sets.filter(s => s.done).length / ex.sets.length) * 100}%`,
                    background: "var(--ph-primary)", borderRadius: 999,
                    transition: "width var(--ph-motion-normal) var(--ph-ease-standard)"
                  }} />
                </div>
                <span className="ph-caption">{ex.sets.filter(s => s.done).length} / {ex.sets.length}</span>
              </div>
            </Card>
          ))}
        </div>

        {/* Muscle load */}
        <Card padding={D.pad}>
          <SectionHead title="Belasting deze sessie" action={<Tag tone="warning">Schouders zwaar</Tag>} />
          <div data-mobile-stack style={{ display: "flex", gap: 16, alignItems: "center" }}>
            <MuscleBalance />
            <div style={{ flex: 1, display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
              {[
                ["Borst", 0.78, "var(--ph-primary)"],
                ["Schouders", 0.92, "var(--ph-data-3)"],
                ["Triceps", 0.6, "var(--ph-primary)"],
                ["Core", 0.2, "var(--ph-data-2)"],
              ].map(([n, v, c]) => (
                <div key={n}>
                  <div style={{ display: "flex", justifyContent: "space-between", fontSize: 12, color: "var(--ph-text-muted)" }}>
                    <span>{n}</span><span>{Math.round(v * 100)}%</span>
                  </div>
                  <div style={{ height: 4, background: "var(--ph-surface-muted)", borderRadius: 999, marginTop: 4, overflow: "hidden" }}>
                    <div style={{ height: "100%", width: `${v * 100}%`, background: c, borderRadius: 999 }} />
                  </div>
                </div>
              ))}
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};

const SetCell = ({ value, done, muted }) => (
  <div className="ph-mono" style={{
    fontSize: 15, fontWeight: done ? 600 : 500,
    color: done ? "var(--ph-text)" : (muted ? "var(--ph-text-faint)" : "var(--ph-text-muted)"),
  }}>{value}</div>
);

/* ───────────── Food + supplements screen ───────────── */
const FoodScreen = ({ lang = "nl", units = "metric", density = "comfortable" }) => {
  const t = useT(lang);
  const D = densityScale(density);
  const [photoOpen, setPhotoOpen] = useStateS(null); // null | "meal" | "label"
  const [photos, setPhotos] = useStateS([
    {
      kind: "meal", time: "07:42", title: "Ontbijt bowl",
      img: "linear-gradient(135deg,#c9a27a 0%, #d8c08a 30%, #6b8a4f 70%, #3f5b34 100%)",
      kcal: 612, macros: "32 P · 67 C · 17 V", confidence: 0.86,
      detected: ["Skyr 0%", "Havermout", "Bosbessen", "Walnoten"],
    },
    {
      kind: "label", time: "yesterday", title: "Whey isolate · pak",
      img: "linear-gradient(135deg,#2a3140 0%, #4a5568 50%, #1a1f2c 100%)",
      kcal: 117, macros: "23 P · 3 C · 1 V (per 30g)", confidence: 0.94,
      detected: ["Eiwit 78g/100g", "Suikers 2.4g", "Vet 1.8g", "Lactose-vrij"],
    },
  ]);

  // Macros (g) and goal kcal
  const macros = {
    protein: { val: 118, goal: 165, c: "var(--ph-data-1)" },
    carbs:   { val: 184, goal: 280, c: "var(--ph-data-3)" },
    fat:     { val: 52,  goal: 75,  c: "var(--ph-data-5)" },
    fiber:   { val: 22,  goal: 35,  c: "var(--ph-data-2)" },
  };
  const kcalLogged = 1842, kcalGoal = 2480;
  const kcalLeft = kcalGoal - kcalLogged;

  const meals = [
    {
      key: "breakfast", title: t("breakfast"), time: "07:42", icon: "sun", kcal: 612,
      items: [
        ["Skyr 0%", "200 g", 110, "20 P · 6 C · 0 V"],
        ["Havermout", "60 g", 228, "8 P · 41 C · 4 V"],
        ["Bosbessen", "120 g", 67, "1 P · 17 C · 0 V"],
        ["Walnoten", "20 g", 132, "3 P · 3 C · 13 V"],
      ],
    },
    {
      key: "lunch", title: t("lunch"), time: "12:40", icon: "leaf", kcal: 530,
      items: [
        ["Quinoa bowl", "350 g", 410, "22 P · 58 C · 12 V"],
        ["Kip filet", "100 g", 165, "31 P · 0 C · 4 V"],
      ],
    },
    {
      key: "snacks", title: t("snacks"), time: "15:10", icon: "apple", kcal: 320,
      items: [
        ["Banaan", "1 stuk", 105, "1 P · 27 C · 0 V"],
        ["Whey shake", "30 g", 117, "23 P · 3 C · 1 V"],
        ["Mandarijn", "2 stuks", 96, "1 P · 24 C · 1 V"],
      ],
    },
    {
      key: "dinner", title: t("dinner"), time: "—", icon: "moon", kcal: 0, items: [], empty: true,
    },
  ];

  // Each supplement names the GAP it fills — i.e. why it's needed today, given what food didn't cover.
  const supplements = [
    { name: "Vitamine D3", dose: "1000 IU", time: t("morning"), done: true, color: "var(--ph-data-3)",
      gap: lang === "nl" ? "Weinig zon · 24% uit voeding" : "Low sun · 24% from food" },
    { name: "Omega-3", dose: "2 g EPA/DHA", time: t("morning"), done: true, color: "var(--ph-data-4)",
      gap: lang === "nl" ? "Geen vette vis vandaag" : "No fatty fish today" },
    { name: "Magnesium glycinate", dose: "300 mg", time: t("evening"), done: false, color: "var(--ph-data-5)",
      gap: lang === "nl" ? "Voeding dekt 68% · slaap-doel" : "Food covers 68% · sleep goal" },
  ];

  // Key micronutrients — % of daily target, split into what came from food vs what supplements topped up.
  // Anything still under 100 with no top-up is a real gap.
  const micros = [
    { key: "protein2", label: lang === "nl" ? "Eiwit" : "Protein", food: 72, supp: 0, unit: "g", target: 165, c: "var(--ph-data-1)" },
    { key: "fiber2",   label: lang === "nl" ? "Vezel" : "Fiber",   food: 63, supp: 0, unit: "g", target: 35, c: "var(--ph-data-2)" },
    { key: "vitd",     label: "Vitamine D",            food: 24, supp: 76, unit: "µg", target: 20, c: "var(--ph-data-3)" },
    { key: "omega",    label: "Omega-3",               food: 18, supp: 82, unit: "g", target: 2, c: "var(--ph-data-4)" },
    { key: "iron",     label: lang === "nl" ? "IJzer" : "Iron",    food: 88, supp: 0, unit: "mg", target: 18, c: "var(--ph-data-6)" },
    { key: "mag",      label: "Magnesium",             food: 68, supp: 0, unit: "mg", target: 400, c: "var(--ph-data-5)" },
    { key: "b12",      label: "B12",                   food: 110, supp: 0, unit: "µg", target: 2.4, c: "var(--ph-data-7)" },
    { key: "calc",     label: lang === "nl" ? "Calcium" : "Calcium", food: 81, supp: 0, unit: "mg", target: 1000, c: "var(--ph-data-2)" },
  ];

  return (
    <div style={{ height: "100%", overflow: "auto", background: "var(--ph-bg)" }}>
      <div style={{ padding: D.pad, maxWidth: 1100, margin: "0 auto", display: "flex", flexDirection: "column", gap: D.vgap }}>

        {/* Header */}
        <div data-mobile-stack style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", gap: 14 }}>
          <div>
            <div className="ph-caption">{t("today_date")}</div>
            <div className="ph-h1" style={{ marginTop: 4 }}>{t("food")}</div>
            <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 4, maxWidth: 520 }}>{t("foodSub")}</div>
          </div>
          <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
            <Button variant="ghost" icon="image" onClick={() => setPhotoOpen("label")}>{lang === "nl" ? "Etiket" : "Label"}</Button>
            <Button variant="ghost" icon="map">{t("barcode")}</Button>
            <Button icon="camera" onClick={() => setPhotoOpen("meal")}>{lang === "nl" ? "Foto maaltijd" : "Photo meal"}</Button>
            <Button variant="secondary" icon="plus">{t("addMeal")}</Button>
          </div>
        </div>

        {/* kcal + macros hero */}
        <Card padding={D.pad}>
          <div data-mobile-stack style={{ display: "grid", gridTemplateColumns: "auto 1fr", gap: D.gap, alignItems: "center" }}>
            <RingGauge value={kcalLogged} max={kcalGoal} size={150} stroke={12} label={t("kcal").toUpperCase()} sublabel={`${kcalLeft} ${t("remaining")}`} />
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 14 }}>
              {Object.entries(macros).map(([k, m]) => {
                const pct = Math.min(1, m.val / m.goal);
                return (
                  <div key={k}>
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
                      <span className="ph-label" style={{ color: m.c }}>{t(k).toUpperCase()}</span>
                      <span className="ph-mono" style={{ fontSize: 13, color: "var(--ph-text-muted)" }}>{m.val}<span style={{ color: "var(--ph-text-faint)" }}> / {m.goal}g</span></span>
                    </div>
                    <div style={{ height: 6, background: "var(--ph-surface-muted)", borderRadius: 999, marginTop: 6, overflow: "hidden" }}>
                      <div style={{ height: "100%", width: `${pct * 100}%`, background: m.c, borderRadius: 999, transition: "width var(--ph-motion-slow) var(--ph-ease-emph)" }} />
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </Card>

        {/* Micronutrients — single collapsible bar; expand for detail */}
        <MicrosBar micros={micros} t={t} lang={lang} D={D} />

        {/* Search + quick add */}
        <div style={{ display: "flex", gap: 10, alignItems: "center", flexWrap: "wrap" }}>
          <div style={{ flex: 1, minWidth: 240, display: "flex", alignItems: "center", gap: 8, padding: "10px 14px", background: "var(--ph-surface)", border: "1px solid var(--ph-border)", borderRadius: "var(--ph-r-pill)" }}>
            <Icon name="target" size={16} color="var(--ph-text-muted)" />
            <span style={{ flex: 1, color: "var(--ph-text-faint)", fontSize: 14 }}>{t("search")}</span>
          </div>
          <Tag icon="trending">{t("recent")}: havermout</Tag>
          <Tag icon="trending">skyr</Tag>
          <Tag icon="trending">walnoten</Tag>
        </div>

        {/* Meals + supplements grid */}
        <div data-grid-collapse style={{ display: "grid", gridTemplateColumns: "1.6fr 1fr", gap: D.gap, alignItems: "flex-start" }}>
          {/* Meals */}
          <div style={{ display: "flex", flexDirection: "column", gap: D.gap }}>
            {meals.map(m => (
              <Card key={m.key} padding={D.pad}>
                <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: m.empty ? 0 : 12 }}>
                  <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                    <div style={{ width: 36, height: 36, borderRadius: "var(--ph-r-md)", background: "var(--ph-primary-soft)", color: "var(--ph-primary)", display: "flex", alignItems: "center", justifyContent: "center" }}>
                      <Icon name={m.icon} size={18} />
                    </div>
                    <div>
                      <div className="ph-h3">{m.title}</div>
                      <div className="ph-caption">{m.time} {!m.empty && `· ${m.kcal} ${t("kcal")}`}</div>
                    </div>
                  </div>
                  <Button size="sm" variant={m.empty ? "primary" : "ghost"} icon="plus">{m.empty ? t("addMeal") : ""}</Button>
                </div>
                {!m.empty && (
                  <div>
                    {m.items.map((it, i) => (
                      <React.Fragment key={i}>
                        <div style={{ display: "grid", gridTemplateColumns: "1fr auto auto", gap: 12, padding: "10px 0", alignItems: "center" }}>
                          <div>
                            <div style={{ fontSize: 14, fontWeight: 500 }}>{it[0]}</div>
                            <div className="ph-caption">{it[1]} · {it[3]}</div>
                          </div>
                          <div className="ph-mono" style={{ fontSize: 13, fontWeight: 600 }}>{it[2]} <span style={{ color: "var(--ph-text-faint)", fontWeight: 400 }}>{t("kcal")}</span></div>
                          <IconButton icon="x" label="remove" />
                        </div>
                        {i < m.items.length - 1 && <div style={{ height: 1, background: "var(--ph-divider)" }} />}
                      </React.Fragment>
                    ))}
                  </div>
                )}
              </Card>
            ))}
          </div>

          {/* Supplements */}
          <div data-unstick-mobile style={{ display: "flex", flexDirection: "column", gap: D.gap, position: "sticky", top: 0 }}>
            <Card padding={D.pad}>
              <SectionHead title={lang === "nl" ? "Foto-log" : "Photo log"} action={<Tag tone="primary" icon="camera">{photos.length}</Tag>} />
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
                {photos.map((p, i) => (
                  <div key={i} style={{ borderRadius: "var(--ph-r-md)", overflow: "hidden", background: "var(--ph-surface-muted)", position: "relative" }}>
                    <div style={{ height: 96, background: p.img }} />
                    <div style={{ position: "absolute", top: 6, left: 6, padding: "2px 8px", borderRadius: 999, background: "rgba(0,0,0,.5)", color: "#fff", fontSize: 10, fontWeight: 500, display: "flex", alignItems: "center", gap: 4 }}>
                      <Icon name={p.kind === "meal" ? "camera" : "image"} size={10} color="#fff" />
                      {p.kind === "meal" ? (lang === "nl" ? "Maaltijd" : "Meal") : (lang === "nl" ? "Etiket" : "Label")}
                    </div>
                    <div style={{ padding: "8px 10px" }}>
                      <div style={{ fontSize: 12, fontWeight: 500, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>{p.title}</div>
                      <div className="ph-caption">{p.kcal} {t("kcal")} · {p.time}</div>
                    </div>
                  </div>
                ))}
                <button onClick={() => setPhotoOpen("meal")} style={{
                  height: 144, border: "1.5px dashed var(--ph-border)", borderRadius: "var(--ph-r-md)",
                  background: "transparent", cursor: "pointer", display: "flex", flexDirection: "column",
                  alignItems: "center", justifyContent: "center", gap: 6, color: "var(--ph-text-muted)",
                  gridColumn: "span 2"
                }}>
                  <Icon name="camera" size={22} />
                  <span style={{ fontSize: 12, fontWeight: 500 }}>{lang === "nl" ? "Nieuwe foto" : "New photo"}</span>
                </button>
              </div>
            </Card>

            <Card padding={D.pad}>
              <SectionHead title={t("supplements")} action={<Tag tone="success" icon="check">{supplements.filter(s => s.done).length}/{supplements.length}</Tag>} />
              <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
                {supplements.map((s, i) => (
                  <div key={i} style={{
                    display: "flex", alignItems: "center", gap: 12,
                    padding: 12, borderRadius: "var(--ph-r-md)",
                    background: s.done ? "var(--ph-success-soft)" : "var(--ph-surface-muted)",
                    opacity: s.done ? 0.85 : 1,
                  }}>
                    <div style={{ width: 32, height: 32, borderRadius: "var(--ph-r-sm)", background: s.color, color: "#fff", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 12, fontWeight: 600 }}>
                      {s.name.split(" ").map(w => w[0]).slice(0, 2).join("")}
                    </div>
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{ fontSize: 14, fontWeight: 500, textDecoration: s.done ? "line-through" : "none" }}>{s.name}</div>
                      <div className="ph-caption">{s.dose} · {s.time}</div>
                    </div>
                    <IconButton icon={s.done ? "check" : "plus"} label="toggle" variant={s.done ? "filled" : "ghost"} />
                  </div>
                ))}
              </div>
              <Button variant="outline" full size="sm" icon="plus" style={{ marginTop: 14 }}>{t("addSupplement")}</Button>
            </Card>

            <Card padding={D.pad}>
              <div className="ph-label">VANDAAG · INZICHT</div>
              <div className="ph-h3" style={{ marginTop: 6 }}>Eiwit nog 47 g</div>
              <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 6 }}>
                Bij dinner past kip + linzen of kwark + noten om je doel te halen.
              </div>
            </Card>
          </div>
        </div>
      </div>

      {photoOpen && (
        <PhotoCapture
          mode={photoOpen}
          lang={lang}
          onClose={() => setPhotoOpen(null)}
          onSave={(p) => { setPhotos([p, ...photos]); setPhotoOpen(null); }}
        />
      )}
    </div>
  );
};

/* ───────────── Photo capture overlay ───────────── */
const PhotoCapture = ({ mode = "meal", lang = "nl", onClose, onSave }) => {
  const t = useT(lang);
  const [stage, setStage] = useStateS("frame"); // frame | analyzing | result
  const isLabel = mode === "label";
  const title = isLabel
    ? (lang === "nl" ? "Foto van etiket" : "Photo of label")
    : (lang === "nl" ? "Foto van maaltijd" : "Photo of meal");

  const result = isLabel ? {
    kind: "label", time: "now", title: "Skyr 0% pak",
    img: "linear-gradient(135deg,#e8edf2 0%, #c9d2dc 60%, #94a3b3 100%)",
    kcal: 63, macros: "11 P · 4 C · 0 V (per 100g)", confidence: 0.92,
    detected: ["Eiwit 11g/100g", "Suikers 3.6g", "Vet 0.2g", "Calcium 130mg"],
  } : {
    kind: "meal", time: "now", title: lang === "nl" ? "Quinoa bowl" : "Quinoa bowl",
    img: "linear-gradient(135deg,#c4a06b 0%, #b8884a 30%, #6b8a4f 70%, #3a5e34 100%)",
    kcal: 487, macros: "26 P · 64 C · 14 V", confidence: 0.81,
    detected: ["Quinoa", "Kip filet", "Avocado", "Spinazie"],
  };

  const shoot = () => { setStage("analyzing"); setTimeout(() => setStage("result"), 1200); };

  return (
    <div style={{
      position: "absolute", inset: 0, background: "rgba(11,20,17,.55)",
      backdropFilter: "blur(8px)", display: "flex", alignItems: "center", justifyContent: "center",
      zIndex: 50, padding: 24,
    }} onClick={onClose}>
      <div onClick={e => e.stopPropagation()} style={{
        background: "var(--ph-surface)", borderRadius: "var(--ph-r-xl)",
        width: "100%", maxWidth: 520, boxShadow: "var(--ph-elev-4)", overflow: "hidden",
      }}>
        {/* Header */}
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", padding: "16px 20px", borderBottom: "1px solid var(--ph-divider)" }}>
          <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
            <Icon name={isLabel ? "image" : "camera"} size={18} color="var(--ph-primary)" />
            <div className="ph-h3">{title}</div>
          </div>
          <IconButton icon="x" label="close" onClick={onClose} />
        </div>

        {/* Viewfinder / preview */}
        <div style={{
          position: "relative", aspectRatio: "4/3",
          background: stage === "frame"
            ? "radial-gradient(circle at 30% 30%, #2a3a32 0%, #0e1714 100%)"
            : result.img,
          display: "flex", alignItems: "center", justifyContent: "center",
        }}>
          {stage === "frame" && (
            <>
              {/* Framing guide */}
              <div style={{
                width: "78%", height: "78%", border: "2px solid rgba(255,255,255,.85)",
                borderRadius: isLabel ? 8 : 999, position: "relative",
              }}>
                {[[0,0],[100,0],[0,100],[100,100]].map(([x,y],i) => (
                  <div key={i} style={{
                    position: "absolute", left: `${x}%`, top: `${y}%`, width: 18, height: 18,
                    borderTop: y===0 ? "3px solid #fff" : "none",
                    borderBottom: y===100 ? "3px solid #fff" : "none",
                    borderLeft: x===0 ? "3px solid #fff" : "none",
                    borderRight: x===100 ? "3px solid #fff" : "none",
                    transform: `translate(${x===0?0:-100}%, ${y===0?0:-100}%)`,
                  }} />
                ))}
              </div>
              <div style={{ position: "absolute", top: 16, left: 16, padding: "6px 10px", borderRadius: 999, background: "rgba(0,0,0,.5)", color: "#fff", fontSize: 11, fontWeight: 500, display: "flex", alignItems: "center", gap: 6 }}>
                <Icon name="sparkle" size={12} color="var(--ph-accent)" />
                {lang === "nl" ? "AI herkenning aan" : "AI recognition on"}
              </div>
              <div style={{ position: "absolute", bottom: 16, left: 0, right: 0, display: "flex", justifyContent: "center", gap: 14 }}>
                <button onClick={() => {}} style={{ width: 44, height: 44, borderRadius: 999, border: "1px solid rgba(255,255,255,.4)", background: "rgba(0,0,0,.3)", color: "#fff", display: "flex", alignItems: "center", justifyContent: "center", cursor: "pointer" }}>
                  <Icon name="image" size={18} color="#fff" />
                </button>
                <button onClick={shoot} style={{ width: 64, height: 64, borderRadius: 999, border: "4px solid rgba(255,255,255,.85)", background: "#fff", cursor: "pointer" }}>
                  <div style={{ width: "100%", height: "100%", borderRadius: 999, background: "var(--ph-primary)" }} />
                </button>
                <button onClick={() => {}} style={{ width: 44, height: 44, borderRadius: 999, border: "1px solid rgba(255,255,255,.4)", background: "rgba(0,0,0,.3)", color: "#fff", display: "flex", alignItems: "center", justifyContent: "center", cursor: "pointer" }}>
                  <Icon name="refresh" size={18} color="#fff" />
                </button>
              </div>
            </>
          )}
          {stage === "analyzing" && (
            <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 12, color: "#fff" }}>
              <div style={{ width: 48, height: 48, borderRadius: 999, border: "3px solid rgba(255,255,255,.25)", borderTopColor: "#fff", animation: "phspin 1s linear infinite" }} />
              <div style={{ fontSize: 14, fontWeight: 500 }}>{lang === "nl" ? "Analyseren…" : "Analyzing…"}</div>
              <style>{`@keyframes phspin { to { transform: rotate(360deg); } }`}</style>
            </div>
          )}
          {stage === "result" && (
            <div style={{ position: "absolute", top: 12, right: 12, padding: "6px 10px", borderRadius: 999, background: "rgba(0,0,0,.55)", color: "#fff", fontSize: 11, fontWeight: 500, display: "flex", alignItems: "center", gap: 6 }}>
              <Icon name="sparkle" size={12} color="var(--ph-accent)" />
              {Math.round(result.confidence * 100)}% {lang === "nl" ? "zeker" : "confident"}
            </div>
          )}
        </div>

        {/* Result */}
        {stage === "result" && (
          <div style={{ padding: 20, display: "flex", flexDirection: "column", gap: 14 }}>
            <div>
              <div className="ph-label">{lang === "nl" ? "HERKEND" : "DETECTED"}</div>
              <div className="ph-h2" style={{ marginTop: 4 }}>{result.title}</div>
            </div>
            <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
              {result.detected.map((d, i) => <Tag key={i} tone={i === 0 ? "primary" : "neutral"} icon={i === 0 ? "check" : undefined}>{d}</Tag>)}
            </div>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10, padding: 14, background: "var(--ph-surface-muted)", borderRadius: "var(--ph-r-md)" }}>
              <div>
                <div className="ph-caption">{t("kcal")}</div>
                <div className="ph-mono" style={{ fontSize: 22, fontWeight: 600 }}>{result.kcal}</div>
              </div>
              <div>
                <div className="ph-caption">{lang === "nl" ? "Macro's" : "Macros"}</div>
                <div className="ph-mono" style={{ fontSize: 14, fontWeight: 500 }}>{result.macros}</div>
              </div>
            </div>
            <div className="ph-caption">
              {lang === "nl" ? "Foto wordt opgeslagen bij dit gerecht. Je kunt ingrediënten later aanpassen." : "Photo saved with this entry. You can edit ingredients later."}
            </div>
            <div style={{ display: "flex", gap: 8, justifyContent: "flex-end" }}>
              <Button variant="ghost" onClick={() => setStage("frame")}>{lang === "nl" ? "Opnieuw" : "Retake"}</Button>
              <Button icon="check" onClick={() => onSave(result)}>{lang === "nl" ? "Opslaan" : "Save"}</Button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

/* ───────────── Tablet rail (medium width — 900px) ───────────── */
const TabletRail = ({ lang = "nl", simplify = false, density = "comfortable" }) => {
  const t = useT(lang);
  const items = [
    ["home", t("today"), true], ["activity", t("track")], ["target", t("plan")],
    ["trending", t("progress")], ["users", t("community")],
  ];
  return (
    <div style={{ display: "grid", gridTemplateRows: "1fr auto", height: "100%", background: "var(--ph-bg)" }}>
      <main style={{ overflow: "auto", padding: 20 }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
          <div>
            <div className="ph-caption">{t("today_date")}</div>
            <div className="ph-h1" style={{ marginTop: 4 }}>{t("today")}</div>
          </div>
          <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
            <Segmented value="week" onChange={() => {}} options={[
              { value: "day", label: lang === "nl" ? "Dag" : "Day" },
              { value: "week", label: "Week" },
              { value: "month", label: lang === "nl" ? "Maand" : "Month" },
            ]} />
            <IconButton icon="bell" label="notifications" />
            <div style={{ width: 36, height: 36, borderRadius: 999, background: "var(--ph-primary-soft)", color: "var(--ph-primary)", display: "flex", alignItems: "center", justifyContent: "center", fontWeight: 600, fontSize: 13, cursor: "pointer" }}>M</div>
          </div>
        </div>

        <Card padding={20} style={{ background: "linear-gradient(160deg, var(--ph-primary-soft), var(--ph-surface))", marginBottom: 16 }}>
          <div style={{ display: "flex", alignItems: "center", gap: 20 }}>
            <RingGauge value={84} size={120} stroke={11} label="READINESS" />
            <div style={{ flex: 1 }}>
              <div className="ph-h3">Goed hersteld</div>
              <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 4 }}>Lichte cardio of mobility past vandaag.</div>
              <div style={{ display: "flex", gap: 8, marginTop: 12, flexWrap: "wrap" }}>
                <Button icon="activity" size="sm">Run</Button>
                <Button variant="secondary" icon="dumbbell" size="sm">Workout</Button>
                <Button variant="secondary" icon="apple" size="sm">Meal</Button>
              </div>
            </div>
          </div>
        </Card>

        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 14, marginBottom: 14 }}>
          <Vital icon="heart" label="HRV" value="68" unit="ms" trend={4} data={[58,62,60,64,66,63,65,67,64,68,70,69,68,72]} tone="primary" />
          <Vital icon="moon" label="SLAAP" value="7u 42m" trend={2} data={[6.5,7.1,6.8,7.4,7.2,7.0,7.6,7.7]} tone="info" />
        </div>
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 14 }}>
          <PlanCard />
          <WeeklyVolume />
        </div>
      </main>

      {/* Bottom bar nav for tablet */}
      <nav style={{
        background: "var(--ph-surface)", borderTop: "1px solid var(--ph-divider)",
        display: "flex", justifyContent: "space-around", padding: "10px 12px",
      }}>
        {items.map(([icn, lbl, active]) => (
          <div key={lbl} style={{
            display: "flex", flexDirection: "column", alignItems: "center", gap: 4,
            padding: "6px 14px", borderRadius: 10, minWidth: 64,
            background: active ? "var(--ph-primary-soft)" : "transparent",
            color: active ? "var(--ph-primary)" : "var(--ph-text-muted)",
            cursor: "pointer",
          }}>
            <Icon name={icn} size={20} />
            <span style={{ fontSize: 11, fontWeight: active ? 600 : 500 }}>{lbl}</span>
          </div>
        ))}
      </nav>
    </div>
  );
};

/* ───────────── Foldable (open) ─── 1080×720 ish, two-pane list+detail ───────────── */
const Foldable = ({ lang = "nl" }) => {
  const t = useT(lang);
  return (
    <div style={{ display: "grid", gridTemplateRows: "1fr auto", height: "100%", background: "var(--ph-bg)" }}>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 8px 1fr", overflow: "hidden" }}>
      {/* Left pane — list */}
      <div style={{ overflow: "auto", padding: 18, borderRight: "1px solid var(--ph-divider)" }}>
        <div className="ph-h2" style={{ marginBottom: 12 }}>{t("track")}</div>
        <Segmented value="strength" onChange={()=>{}} options={[
          { value: "all", label: "All" },
          { value: "cardio", label: "Cardio" },
          { value: "strength", label: t("strength") },
          { value: "food", label: t("food").split(" ")[0] },
        ]} />
        <div style={{ height: 14 }} />
        <Card padding={14}>
          <ListRow icon="dumbbell" title={t("pushDay")} meta="Vandaag · in progress" value="3/14" sub={t("setsLogged")} accent="var(--ph-primary-soft)" />
          <div style={{ height: 1, background: "var(--ph-divider)" }} />
          <ListRow icon="footprints" title="Ochtendloop" meta="Vandaag · 38 min" value="6.4 km" sub="Z2" />
          <div style={{ height: 1, background: "var(--ph-divider)" }} />
          <ListRow icon="apple" title="Lunch" meta="12:40" value="612" sub="kcal" accent="var(--ph-success-soft)" />
          <div style={{ height: 1, background: "var(--ph-divider)" }} />
          <ListRow icon="bike" title="Roadbike" meta="Zaterdag · 1u 24m" value="32.1 km" sub="24 km/u avg" accent="var(--ph-info-soft)" />
        </Card>
        <div style={{ height: 14 }} />
        <Heatmap rows={7} cols={10} />
      </div>

      {/* Hinge */}
      <div style={{ background: "linear-gradient(90deg, transparent, rgba(0,0,0,.06), transparent)" }} />

      {/* Right pane — detail (Strength session, condensed) */}
      <div style={{ overflow: "auto", padding: 18, display: "flex", flexDirection: "column", gap: 14 }}>
        <div>
          <div className="ph-caption">In progress</div>
          <div className="ph-h2" style={{ marginTop: 2 }}>{t("pushDay")}</div>
        </div>
        <Card padding={16} style={{ background: "linear-gradient(135deg, var(--ph-primary-soft), var(--ph-surface) 70%)" }}>
          <div style={{ display: "flex", gap: 14, alignItems: "center" }}>
            <RingGauge value={48} max={120} size={84} stroke={9} label={t("rest")} sublabel="0:48" color="var(--ph-data-3)" />
            <div>
              <div className="ph-label">{t("lastSet")}</div>
              <div className="ph-h3">Bench · 75 kg × 5</div>
              <div className="ph-caption">RIR 1 · target 4×5 @ 75</div>
            </div>
          </div>
        </Card>
        <Card padding={16}>
          <div className="ph-h3" style={{ marginBottom: 8 }}>Bench press</div>
          <div className="ph-caption" style={{ marginBottom: 10 }}>3/4 sets</div>
          {[[60,8,3,true],[70,6,2,true],[75,5,1,true],[75,5,1,false]].map((s,i) => (
            <div key={i} style={{ display: "grid", gridTemplateColumns: "30px 1fr 60px 60px", gap: 8, alignItems: "center", padding: "8px 0", borderTop: i===0 ? "none" : "1px solid var(--ph-divider)" }}>
              <span className="ph-mono" style={{ color: "var(--ph-text-muted)" }}>{i+1}</span>
              <span className="ph-mono" style={{ fontWeight: 500 }}>{s[0]} kg × {s[1]}</span>
              <span className="ph-caption">RIR {s[2]}</span>
              {s[3] ? <Tag tone="success" icon="check">Done</Tag> : <Tag tone="primary">Now</Tag>}
            </div>
          ))}
        </Card>
        <Card padding={16}>
          <SectionHead title="Belasting" />
          <div style={{ display: "flex", gap: 14 }}>
            <MuscleBalance />
            <div style={{ flex: 1, display: "flex", flexDirection: "column", gap: 8 }}>
              <ZoneBar zones={[5, 12, 28, 40, 15]} />
              <div className="ph-caption">RPE verdeling deze sessie</div>
            </div>
          </div>
        </Card>
      </div>
      </div>

      {/* Bottom bar nav for foldable */}
      <nav style={{
        background: "var(--ph-surface)", borderTop: "1px solid var(--ph-divider)",
        display: "flex", justifyContent: "space-around", padding: "10px 12px",
      }}>
        {[
          ["home", t("today")],
          ["activity", t("track"), true],
          ["target", t("plan")],
          ["trending", t("progress")],
          ["users", t("community")],
        ].map(([icn, lbl, active], idx) => (
          <div key={idx} style={{
            display: "flex", flexDirection: "column", alignItems: "center", gap: 4,
            padding: "6px 14px", borderRadius: 10, minWidth: 72,
            background: active ? "var(--ph-primary-soft)" : "transparent",
            color: active ? "var(--ph-primary)" : "var(--ph-text-muted)",
            cursor: "pointer",
          }}>
            <Icon name={icn} size={20} />
            <span style={{ fontSize: 11, fontWeight: active ? 600 : 500 }}>{lbl}</span>
          </div>
        ))}
      </nav>
    </div>
  );
};

/* ───────────── Mobile frame wrapper ─────────────
   Renders any responsive screen inside a 390-wide column so the same
   component code handles both desktop and mobile artboards. The screen's
   own grid rules collapse naturally at this width via CSS we add inline. */
const MobileFrame = ({ children, width = 390, height = 844 }) => (
  <div style={{ width, height, background: "var(--ph-bg)", borderRadius: 0, overflow: "hidden", position: "relative" }}>
    {/* Status bar */}
    <div style={{
      height: 28, padding: "0 22px", display: "flex", alignItems: "center", justifyContent: "space-between",
      fontSize: 12, fontWeight: 600, color: "var(--ph-text)", background: "var(--ph-bg)"
    }}>
      <span>9:41</span>
      <span style={{ display: "flex", gap: 6, alignItems: "center" }}>
        <Icon name="activity" size={11} />
        <Icon name="sun" size={11} />
        <span style={{
          width: 22, height: 11, border: "1px solid var(--ph-text)", borderRadius: 3, position: "relative"
        }}>
          <span style={{ position: "absolute", inset: 1.5, width: "70%", background: "var(--ph-text)", borderRadius: 1 }} />
        </span>
      </span>
    </div>
    <div style={{ height: height - 28, overflow: "hidden", position: "relative" }}>
      <style>{`
        .ph-mobile-scope [data-grid-collapse] { grid-template-columns: 1fr !important; }
        .ph-mobile-scope [data-hide-mobile] { display: none !important; }
        .ph-mobile-scope [data-mobile-stack] { display: flex !important; flex-direction: column !important; align-items: stretch !important; }
        .ph-mobile-scope [data-mobile-stack] > * { width: 100%; }
        .ph-mobile-scope [data-unstick-mobile] { position: static !important; }
        .ph-mobile-scope .ph-h1 { font-size: 26px !important; line-height: 1.15 !important; }
        .ph-mobile-scope .ph-h2 { font-size: 20px !important; }
        .ph-mobile-scope { font-size: 14px; }
      `}</style>
      <div className="ph-mobile-scope" style={{ height: "100%" }}>
        {children}
      </div>
    </div>
  </div>
);

/* ───────────── Tablet Today ─────────────
   Two-pane Today layout for ~834×1194 portrait. Reuses AppShell-style content
   but with the rail nav and a tighter grid. */
const TabletToday = ({ lang = "nl", simplify = false, density = "comfortable" }) => {
  const t = useT(lang);
  return (
    <div style={{ display: "grid", gridTemplateRows: "1fr auto", height: "100%", background: "var(--ph-bg)" }}>
      <div style={{ overflow: "auto", padding: 24 }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 16 }}>
          <div>
            <div className="ph-caption">{t("today_date") || "Vandaag"}</div>
            <div className="ph-h1" style={{ marginTop: 4 }}>{t("readiness") || (lang === "nl" ? "Goed hersteld" : "Well recovered")}</div>
          </div>
          <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
            <IconButton icon="bell" label="notifications" />
            <div style={{ width: 36, height: 36, borderRadius: 999, background: "var(--ph-primary-soft)", color: "var(--ph-primary)", display: "flex", alignItems: "center", justifyContent: "center", fontWeight: 600, fontSize: 13, cursor: "pointer" }}>M</div>
          </div>
        </div>

        <Card padding={20} style={{ marginBottom: 14 }}>
          <div style={{ display: "flex", alignItems: "center", gap: 24 }}>
            <TripleRing size={150} stroke={11} rings={[
              { value: 82, max: 100, color: "var(--ph-primary)", key: "rec" },
              { value: 76, max: 100, color: "var(--ph-info, #5b8aa3)", key: "sleep" },
              { value: 64, max: 100, color: "var(--ph-data-5)", key: "load" },
            ]} />
            <div style={{ flex: 1 }}>
              <div className="ph-h2">82</div>
              <div className="ph-caption" style={{ marginBottom: 10 }}>{lang === "nl" ? "Hersteldscore" : "Recovery score"}</div>
              <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
                <Tag tone="primary" icon="heart">HRV 68</Tag>
                <Tag tone="info" icon="moon">7u 42m</Tag>
                <Tag icon="activity">Load 64</Tag>
              </div>
              {!simplify && (
                <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 12, maxWidth: 420 }}>
                  {lang === "nl" ? "Lichte cardio of mobility past vandaag." : "Light cardio or mobility fits today."}
                </div>
              )}
            </div>
          </div>
        </Card>

        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 14, marginBottom: 14 }}>
          <Vital icon="heart" label="HRV" value="68" unit="ms" trend={4} data={[58,62,60,64,66,63,65,67,64,68,70,69,68,72]} tone="primary" />
          <Vital icon="moon" label={lang === "nl" ? "SLAAP" : "SLEEP"} value="7u 42m" trend={2} data={[6.5,7.1,6.8,7.4,7.2,7.0,7.6,7.7]} tone="info" />
        </div>
        {!simplify && (
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 14 }}>
            <PlanCard />
            <WeeklyVolume />
          </div>
        )}
      </div>

      {/* Bottom bar nav for tablet */}
      <nav style={{
        background: "var(--ph-surface)", borderTop: "1px solid var(--ph-divider)",
        display: "flex", justifyContent: "space-around", padding: "10px 12px",
      }}>
        {[
          { i: "sun", l: t("today"), active: true },
          { i: "plus", l: t("track") },
          { i: "calendar", l: t("plan") },
          { i: "trending", l: t("progress") },
          { i: "users", l: t("community") },
        ].map((n, idx) => (
          <div key={idx} style={{
            display: "flex", flexDirection: "column", alignItems: "center", gap: 4,
            padding: "6px 14px", borderRadius: 10, minWidth: 64,
            background: n.active ? "var(--ph-primary-soft)" : "transparent",
            color: n.active ? "var(--ph-primary)" : "var(--ph-text-muted)",
            cursor: "pointer",
          }}>
            <Icon name={n.i} size={20} />
            <span style={{ fontSize: 11, fontWeight: n.active ? 600 : 500 }}>{n.l}</span>
          </div>
        ))}
      </nav>
    </div>
  );
};

/* ───────────── Community / Social screen ─────────────
   Three columns on desktop, stacks on mobile via data-grid-collapse:
   - Feed: friends' activities, your own shareable cards, kudos
   - Events: races/runs/rides matched to YOUR profile (location + sport prefs)
   - Challenges: group goals, leaderboards
   The privacy is data-density tweakable — full reveal vs muted-blur. */
const CommunityScreen = ({ lang = "nl", density = "comfortable" }) => {
  const t = useT(lang);
  const D = densityScale(density);
  const isNL = lang === "nl";

  // Friends feed — what they did + how it lands. Tasteful, not noisy.
  const feed = [
    {
      id: 1, type: "run", who: "Eva van Loon", whoInit: "EV",
      verb: isNL ? "liep een tempo run" : "ran a tempo session",
      time: isNL ? "12 min geleden" : "12 min ago", location: "Vondelpark",
      stat1: ["Afstand", "8.4 km"], stat2: ["Tempo", "4:38 /km"], stat3: ["HR avg", "162"],
      route: true, kudos: 12, kudosed: false, comments: 2,
      tone: "var(--ph-data-3)",
    },
    {
      id: 2, type: "achievement", who: "Jij", whoInit: "M", isYou: true,
      verb: isNL ? "haalde een nieuw record" : "hit a new PR",
      time: isNL ? "vandaag · 09:14" : "today · 09:14", location: "Bench press",
      headline: "82.5 kg × 5", sub: isNL ? "+5 kg t.o.v. vorige cyclus" : "+5 kg vs previous cycle",
      shareable: true, kudos: 5, kudosed: true, comments: 0,
      tone: "var(--ph-primary)",
    },
    {
      id: 3, type: "ride", who: "Bram Hofman", whoInit: "BH",
      verb: isNL ? "fietste een rustige Z2" : "rode an easy Z2",
      time: isNL ? "2u geleden" : "2h ago", location: "Amstelveen → Ouderkerk",
      stat1: ["Afstand", "42.1 km"], stat2: ["Tempo", "28.4 km/u"], stat3: ["Klim", "84 m"],
      route: true, kudos: 4, kudosed: true, comments: 1,
      tone: "var(--ph-data-4)",
    },
    {
      id: 4, type: "milestone", who: "Lara Brun", whoInit: "LB",
      verb: isNL ? "haalde 30 dagen op rij beweging" : "hit a 30-day move streak",
      time: isNL ? "gisteren" : "yesterday", location: "",
      headline: isNL ? "30 dagen streak" : "30-day streak",
      sub: isNL ? "Gemiddeld 47 actieve minuten per dag" : "Avg 47 active min / day",
      streak: true, kudos: 23, kudosed: false, comments: 4,
      tone: "var(--ph-data-5)",
    },
  ];

  // Events filtered to user profile: lives in Amsterdam, runs Z2 weekly, lifts.
  // "match" = relevance score (0–1).
  const events = [
    {
      id: 1, kind: "run", title: "Vondelpark 10K",
      date: { d: 18, m: isNL ? "MEI" : "MAY" }, dist: "9 km away",
      meta: ["10 km", "08:00", isNL ? "Vlak parcours" : "Flat"],
      match: 0.94, matchReason: isNL ? "Past bij je Z2-loopvolume" : "Matches your Z2 run volume",
      attendees: 412, friends: 3, fee: "€18",
      tone: "var(--ph-data-3)",
    },
    {
      id: 2, kind: "race", title: "Damloop by Night 16K",
      date: { d: 7, m: isNL ? "JUN" : "JUN" }, dist: "12 km away",
      meta: ["16 km", "20:00", isNL ? "Zaanstreek" : "Zaanstreek"],
      match: 0.81, matchReason: isNL ? "Iets boven je longest run · 6 wkn opbouw" : "Slightly above your longest run · 6 wk build",
      attendees: 6800, friends: 1, fee: "€32",
      tone: "var(--ph-data-5)",
    },
    {
      id: 3, kind: "ride", title: "Tour de Waterland",
      date: { d: 25, m: isNL ? "MEI" : "MAY" }, dist: "5 km away",
      meta: ["80 km", isNL ? "groep" : "group", "Z2"],
      match: 0.66, matchReason: isNL ? "Buiten je gewone radius — uitdagend" : "Beyond your usual radius — challenging",
      attendees: 220, friends: 0, fee: isNL ? "Gratis" : "Free",
      tone: "var(--ph-data-4)",
    },
    {
      id: 4, kind: "lift", title: isNL ? "Open powerlifting · ATC" : "Open powerlifting · ATC",
      date: { d: 14, m: isNL ? "JUN" : "JUN" }, dist: "3 km away",
      meta: [isNL ? "raw" : "raw", "-83 kg", isNL ? "intern" : "in-house"],
      match: 0.58, matchReason: isNL ? "Eerste meet — laagdrempelig" : "First meet — beginner friendly",
      attendees: 42, friends: 0, fee: "€25",
      tone: "var(--ph-primary)",
    },
  ];

  const challenges = [
    {
      id: 1, title: isNL ? "Mei · 100 km hardlopen" : "May · 100 km run",
      progress: 64, target: 100, unit: "km",
      members: 8, you: 3, daysLeft: 11,
      tone: "var(--ph-data-3)",
    },
    {
      id: 2, title: isNL ? "Slaap-streak · team" : "Sleep streak · team",
      progress: 22, target: 30, unit: isNL ? "dagen" : "days",
      members: 5, you: 2, daysLeft: 8,
      tone: "var(--ph-info, #5b8aa3)",
    },
  ];

  return (
    <div style={{ height: "100%", overflow: "auto", background: "var(--ph-bg)" }}>
      <div style={{ padding: D.pad, maxWidth: 1280, margin: "0 auto", display: "flex", flexDirection: "column", gap: D.vgap }}>

        {/* Header */}
        <div data-mobile-stack style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", gap: 14 }}>
          <div>
            <div className="ph-caption">{t("today_date")}</div>
            <div className="ph-h1" style={{ marginTop: 4 }}>{t("community")}</div>
            <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 4, maxWidth: 540 }}>
              {isNL
                ? "Deel wat je traint, vind events die bij je passen, daag vrienden uit."
                : "Share what you train, find events that fit you, challenge your friends."}
            </div>
          </div>
          <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
            <Button variant="ghost" icon="user">{isNL ? "Vrienden" : "Friends"}</Button>
            <Button variant="secondary" icon="calendar">{isNL ? "Mijn events" : "My events"}</Button>
            <Button icon="plus">{isNL ? "Deel iets" : "Share"}</Button>
          </div>
        </div>

        {/* Share-your-status hero — your readiness + streak as a tappable card */}
        <Card padding={D.pad} style={{ background: "linear-gradient(135deg, var(--ph-primary-soft) 0%, var(--ph-surface) 65%)" }}>
          <div data-mobile-stack style={{ display: "flex", alignItems: "center", gap: 24 }}>
            <TripleRing size={120} stroke={9} rings={[
              { value: 82, max: 100, color: "var(--ph-primary)", key: "rec" },
              { value: 76, max: 100, color: "var(--ph-info, #5b8aa3)", key: "sleep" },
              { value: 64, max: 100, color: "var(--ph-data-5)", key: "load" },
            ]} />
            <div style={{ flex: 1, minWidth: 220 }}>
              <div className="ph-label" style={{ color: "var(--ph-primary)" }}>{isNL ? "JOUW STATUS · DEELBAAR" : "YOUR STATUS · SHAREABLE"}</div>
              <div className="ph-h2" style={{ marginTop: 4 }}>{isNL ? "Goed hersteld · 14 dagen streak" : "Well recovered · 14-day streak"}</div>
              <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 6, maxWidth: 480 }}>
                {isNL
                  ? "HRV 68 · Slaap 7u 42m · Load 64. Klop op de schouder voor wie je vandaag inspireerde."
                  : "HRV 68 · Sleep 7h 42m · Load 64. Tap to give kudos to who inspired you today."}
              </div>
            </div>
            <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
              <Button icon="plus" size="sm">{isNL ? "Deel" : "Share"}</Button>
              <Button variant="outline" icon="settings" size="sm">{isNL ? "Privacy" : "Privacy"}</Button>
            </div>
          </div>
        </Card>

        {/* Three-column grid: feed | events | challenges */}
        <div data-grid-collapse style={{ display: "grid", gridTemplateColumns: "1.4fr 1fr 1fr", gap: D.gap, alignItems: "flex-start" }}>

          {/* ── Feed ── */}
          <div style={{ display: "flex", flexDirection: "column", gap: D.gap }}>
            <FeaturedActivity isNL={isNL} />
            <Card padding={D.pad}>
              <SectionHead
                title={isNL ? "Activiteit" : "Feed"}
                subtitle={isNL ? "Vrienden + jij" : "Friends + you"}
                action={<Segmented value="all" onChange={() => {}} options={[
                  { value: "all", label: isNL ? "Alles" : "All" },
                  { value: "friends", label: isNL ? "Vrienden" : "Friends" },
                  { value: "you", label: isNL ? "Jij" : "You" },
                ]} />}
              />
              <div style={{ display: "flex", flexDirection: "column", gap: 14 }}>
                {feed.map(item => (
                  <FeedCard key={item.id} item={item} isNL={isNL} />
                ))}
                <Button variant="ghost" full icon="trending">{isNL ? "Toon meer" : "Show more"}</Button>
              </div>
            </Card>
          </div>

          {/* ── Events ── */}
          <div style={{ display: "flex", flexDirection: "column", gap: D.gap }}>
            <Card padding={D.pad}>
              <SectionHead
                title={isNL ? "Events voor jou" : "Events for you"}
                subtitle={isNL ? "Op basis van je profiel + locatie" : "Matched to your profile + location"}
                action={<Tag tone="primary" icon="calendar">{events.length}</Tag>}
              />
              <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
                {events.map(e => (
                  <EventCard key={e.id} ev={e} isNL={isNL} />
                ))}
              </div>
              <Button variant="outline" full size="sm" icon="map" style={{ marginTop: 14 }}>
                {isNL ? "Open kaart" : "Open map"}
              </Button>
            </Card>
          </div>

          {/* ── Challenges + weekly stats + leaderboard ── */}
          <div style={{ display: "flex", flexDirection: "column", gap: D.gap }}>
            <WeekStats isNL={isNL} />
            <LocalLegend isNL={isNL} />
            <Card padding={D.pad}>
              <SectionHead
                title={isNL ? "Uitdagingen" : "Challenges"}
                subtitle={isNL ? "Groep · langlopend" : "Group · ongoing"}
                action={<Button variant="ghost" size="sm" icon="plus" />}
              />
              <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
                {challenges.map(c => (
                  <ChallengeCard key={c.id} c={c} isNL={isNL} />
                ))}
              </div>
            </Card>

            <Card padding={D.pad}>
              <div className="ph-label">{isNL ? "VRIENDEN · 12" : "FRIENDS · 12"}</div>
              <div style={{ display: "flex", marginTop: 10, marginBottom: 12 }}>
                {["EV","BH","LB","JS","AT","M+"].map((n, i) => (
                  <div key={i} style={{
                    width: 36, height: 36, borderRadius: "50%",
                    background: i === 5 ? "var(--ph-surface-muted)" : `oklch(70% 0.08 ${(i*55) % 360})`,
                    color: i === 5 ? "var(--ph-text-muted)" : "#fff",
                    display: "flex", alignItems: "center", justifyContent: "center",
                    fontSize: 11, fontWeight: 600,
                    border: "2px solid var(--ph-surface)",
                    marginLeft: i === 0 ? 0 : -10,
                  }}>{n}</div>
                ))}
              </div>
              <Button variant="outline" full size="sm" icon="plus">{isNL ? "Vriend toevoegen" : "Add friend"}</Button>
            </Card>

            <Card padding={D.pad}>
              <div className="ph-label">{isNL ? "PRIVACY" : "PRIVACY"}</div>
              <div className="ph-h3" style={{ marginTop: 4 }}>{isNL ? "Jij bepaalt wat zichtbaar is" : "You decide what's visible"}</div>
              <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 8 }}>
                {isNL
                  ? "Splits per type: trainingen, voeding, gewicht, rustdata. Standaard alleen voor vrienden."
                  : "Split by type: workouts, food, weight, rest data. Friends-only by default."}
              </div>
              <Button variant="ghost" full size="sm" icon="settings" iconRight="chevronRight" style={{ marginTop: 10 }}>
                {isNL ? "Open privacy" : "Open privacy"}
              </Button>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};

/* Feed item — one friend's (or your own) activity */
const FeedCard = ({ item, isNL }) => (
  <div style={{
    border: "1px solid var(--ph-divider)", borderRadius: "var(--ph-r-md)",
    padding: 14, background: item.isYou ? "var(--ph-primary-soft)" : "var(--ph-surface)",
  }}>
    {/* Header row */}
    <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 10 }}>
      <div style={{
        width: 36, height: 36, borderRadius: "50%",
        background: item.tone, color: "#fff",
        display: "flex", alignItems: "center", justifyContent: "center",
        fontSize: 12, fontWeight: 700,
      }}>{item.whoInit}</div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 14, fontWeight: 600 }}>
          {item.who} <span style={{ fontWeight: 400, color: "var(--ph-text-muted)" }}>{item.verb}</span>
        </div>
        <div className="ph-caption">{item.time}{item.location && ` · ${item.location}`}</div>
      </div>
      {item.shareable && <Tag tone="primary" icon="trending">PR</Tag>}
      {item.streak && <Tag tone="success" icon="flame">{isNL ? "streak" : "streak"}</Tag>}
    </div>

    {/* Body — depends on type */}
    {item.headline && (
      <div style={{ padding: "12px 14px", background: "var(--ph-surface-muted)", borderRadius: "var(--ph-r-sm)", marginBottom: 10 }}>
        <div className="ph-mono" style={{ fontSize: 22, fontWeight: 700 }}>{item.headline}</div>
        <div className="ph-caption" style={{ marginTop: 2 }}>{item.sub}</div>
      </div>
    )}
    {item.stat1 && (
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 10, marginBottom: item.route ? 10 : 0 }}>
        {[item.stat1, item.stat2, item.stat3].map(([k, v], i) => (
          <div key={i}>
            <div className="ph-caption">{k}</div>
            <div className="ph-mono" style={{ fontSize: 15, fontWeight: 600 }}>{v}</div>
          </div>
        ))}
      </div>
    )}
    {item.route && (
      <div style={{
        height: 70, borderRadius: "var(--ph-r-sm)",
        background: `linear-gradient(180deg, color-mix(in oklch, ${item.tone} 18%, var(--ph-surface)), var(--ph-surface))`,
        position: "relative", overflow: "hidden", marginBottom: 10,
      }}>
        <svg viewBox="0 0 200 70" preserveAspectRatio="none" style={{ width: "100%", height: "100%" }}>
          <path d="M 8,55 Q 30,20 55,38 T 105,28 Q 130,15 155,42 T 192,30"
                stroke={item.tone} strokeWidth="2.5" fill="none" strokeLinecap="round" />
          <circle cx="8" cy="55" r="4" fill={item.tone} />
          <circle cx="192" cy="30" r="4" fill={item.tone} />
        </svg>
      </div>
    )}

    {/* Actions */}
    <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
      <button style={{
        display: "flex", alignItems: "center", gap: 6,
        padding: "6px 12px", borderRadius: 999,
        background: item.kudosed ? "var(--ph-primary-soft)" : "transparent",
        color: item.kudosed ? "var(--ph-primary)" : "var(--ph-text-muted)",
        border: item.kudosed ? "1px solid var(--ph-primary)" : "1px solid var(--ph-border)",
        fontSize: 13, fontWeight: 500, cursor: "pointer",
      }}>
        <Icon name="heart" size={14} color={item.kudosed ? "var(--ph-primary)" : "currentColor"} />
        {item.kudos}
      </button>
      <button style={{
        display: "flex", alignItems: "center", gap: 6,
        padding: "6px 12px", borderRadius: 999, background: "transparent",
        color: "var(--ph-text-muted)", border: "1px solid var(--ph-border)",
        fontSize: 13, fontWeight: 500, cursor: "pointer",
      }}>
        <Icon name="map" size={14} />
        {item.comments}
      </button>
      <div style={{ flex: 1 }} />
      <IconButton icon="plus" label="more" />
    </div>
  </div>
);

/* Event card — date stamp on the left, details on the right */
const EventCard = ({ ev, isNL }) => (
  <div style={{
    display: "flex", gap: 12, padding: 12,
    border: "1px solid var(--ph-divider)", borderRadius: "var(--ph-r-md)",
    background: "var(--ph-surface)",
  }}>
    {/* Date stamp */}
    <div style={{
      width: 56, flexShrink: 0,
      borderRadius: "var(--ph-r-sm)",
      background: `color-mix(in oklch, ${ev.tone} 14%, var(--ph-surface-muted))`,
      display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center",
      padding: "8px 4px",
    }}>
      <div className="ph-mono" style={{ fontSize: 22, fontWeight: 700, color: ev.tone, lineHeight: 1 }}>{ev.date.d}</div>
      <div style={{ fontSize: 10, fontWeight: 600, color: ev.tone, letterSpacing: ".08em", marginTop: 4 }}>{ev.date.m}</div>
    </div>

    {/* Details */}
    <div style={{ flex: 1, minWidth: 0 }}>
      <div style={{ display: "flex", alignItems: "center", gap: 6, marginBottom: 2 }}>
        <Icon name={ev.kind === "ride" ? "bike" : ev.kind === "lift" ? "dumbbell" : "footprints"} size={13} color={ev.tone} />
        <span style={{ fontSize: 14, fontWeight: 600, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{ev.title}</span>
      </div>
      <div className="ph-caption" style={{ marginBottom: 8 }}>
        {ev.meta.join(" · ")} · <span>{ev.dist}</span>
      </div>

      {/* Match score row */}
      <div style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 8 }}>
        <div style={{
          flex: 1, height: 4, background: "var(--ph-surface-muted)", borderRadius: 999, overflow: "hidden",
        }}>
          <div style={{ width: `${ev.match * 100}%`, height: "100%", background: ev.tone }} />
        </div>
        <span className="ph-mono" style={{ fontSize: 11, fontWeight: 600, color: ev.tone }}>
          {Math.round(ev.match * 100)}% {isNL ? "match" : "match"}
        </span>
      </div>
      <div className="ph-caption" style={{ fontStyle: "italic", marginBottom: 10 }}>{ev.matchReason}</div>

      {/* Footer */}
      <div style={{ display: "flex", alignItems: "center", gap: 8, fontSize: 11, color: "var(--ph-text-muted)" }}>
        <span>👥 {ev.attendees}</span>
        {ev.friends > 0 && <span style={{ color: "var(--ph-primary)", fontWeight: 600 }}>· {ev.friends} {isNL ? "vrienden" : "friends"}</span>}
        <div style={{ flex: 1 }} />
        <span style={{ fontWeight: 600, color: "var(--ph-text)" }}>{ev.fee}</span>
        <Button size="sm" variant="outline">{isNL ? "Bekijk" : "View"}</Button>
      </div>
    </div>
  </div>
);

/* Challenge card — group goal with progress + your contribution */
const ChallengeCard = ({ c, isNL }) => {
  const pct = Math.min(1, c.progress / c.target);
  return (
    <div style={{
      padding: 14,
      border: "1px solid var(--ph-divider)", borderRadius: "var(--ph-r-md)",
      background: "var(--ph-surface)",
    }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", gap: 10, marginBottom: 8 }}>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ fontSize: 14, fontWeight: 600 }}>{c.title}</div>
          <div className="ph-caption">{c.members} {isNL ? "leden" : "members"} · {c.daysLeft} {isNL ? "dagen left" : "days left"}</div>
        </div>
        <Tag tone="primary">{Math.round(pct * 100)}%</Tag>
      </div>
      <div style={{ height: 8, background: "var(--ph-surface-muted)", borderRadius: 999, overflow: "hidden", marginBottom: 8 }}>
        <div style={{ width: `${pct * 100}%`, height: "100%", background: c.tone, borderRadius: 999 }} />
      </div>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", fontSize: 12, color: "var(--ph-text-muted)" }}>
        <span><span className="ph-mono" style={{ color: "var(--ph-text)", fontWeight: 600 }}>{c.progress}</span> / {c.target} {c.unit}</span>
        <span>{isNL ? "Jij" : "You"}: <span style={{ color: c.tone, fontWeight: 600 }}>+{c.you} {c.unit}</span></span>
      </div>
    </div>
  );
};

/* ───────────── Strava-inspired: Featured Activity ─────────────
   The expanded version of a feed item — used for the top run of the day.
   Big map, splits table, HR-zone breakdown, segments/PRs, photo strip. */
const FeaturedActivity = ({ isNL }) => {
  // Splits per km — pace as mm:ss, hr in bpm
  const splits = [
    { km: 1, paceSec: 285, hr: 148, fast: false },
    { km: 2, paceSec: 278, hr: 156, fast: false },
    { km: 3, paceSec: 268, hr: 162, fast: true },
    { km: 4, paceSec: 270, hr: 164, fast: false },
    { km: 5, paceSec: 282, hr: 161, fast: false },
    { km: 6, paceSec: 276, hr: 159, fast: false },
    { km: 7, paceSec: 270, hr: 162, fast: false },
    { km: 8, paceSec: 264, hr: 168, fast: true },
  ];
  const fastestPace = Math.min(...splits.map(s => s.paceSec));
  const slowestPace = Math.max(...splits.map(s => s.paceSec));
  const fmtPace = sec => `${Math.floor(sec / 60)}:${String(sec % 60).padStart(2, "0")}`;

  // HR zones (% of activity time)
  const zones = [
    { z: "Z1", color: "var(--ph-data-3)", pct: 4 },
    { z: "Z2", color: "var(--ph-data-2)", pct: 18 },
    { z: "Z3", color: "var(--ph-data-7)", pct: 38 },
    { z: "Z4", color: "var(--ph-data-5)", pct: 32 },
    { z: "Z5", color: "var(--ph-data-6)", pct: 8 },
  ];

  // Segments / PRs hit during activity
  const segments = [
    { name: "Vondelpark Loop · 3.2 km", time: "13:47", pr: "2nd", delta: "-12s", isPr: false },
    { name: "Museumplein Sprint · 800 m", time: "2:54", pr: "PR", delta: "-4s", isPr: true },
    { name: "Apollolaan · 1.5 km", time: "6:18", pr: "5/12", delta: "+8s", isPr: false },
  ];

  return (
    <div style={{
      border: "1px solid var(--ph-divider)", borderRadius: "var(--ph-r-md)",
      background: "var(--ph-surface)", overflow: "hidden",
    }}>
      {/* Header */}
      <div style={{ padding: "14px 16px", display: "flex", alignItems: "center", gap: 10 }}>
        <div style={{
          width: 36, height: 36, borderRadius: "50%",
          background: "var(--ph-data-3)", color: "#fff",
          display: "flex", alignItems: "center", justifyContent: "center",
          fontSize: 12, fontWeight: 700,
        }}>EV</div>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ fontSize: 14, fontWeight: 600 }}>
            Eva van Loon <span style={{ fontWeight: 400, color: "var(--ph-text-muted)" }}>{isNL ? "liep een tempo run" : "ran a tempo session"}</span>
          </div>
          <div className="ph-caption">{isNL ? "12 min geleden · Vondelpark" : "12 min ago · Vondelpark"}</div>
        </div>
        <Tag tone="primary" icon="trending">PR</Tag>
        <IconButton icon="settings" label="more" />
      </div>

      {/* Title + key metrics */}
      <div style={{ padding: "0 16px 14px" }}>
        <div className="ph-h3" style={{ marginBottom: 12 }}>{isNL ? "Tempo verkenning" : "Tempo exploration"}</div>
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr 1fr", gap: 4 }}>
          {[
            [isNL ? "Afstand" : "Distance", "8.4", "km"],
            [isNL ? "Pace" : "Pace", "4:38", "/km"],
            [isNL ? "Tijd" : "Time", "38:54", ""],
            [isNL ? "Hoogte" : "Elevation", "42", "m"],
          ].map(([k, v, u], i) => (
            <div key={i} style={{ borderRight: i < 3 ? "1px solid var(--ph-divider)" : "none", paddingRight: 8 }}>
              <div className="ph-caption">{k}</div>
              <div style={{ fontSize: 22, fontWeight: 600, fontFamily: "var(--ph-font-mono)", lineHeight: 1.1, marginTop: 4 }}>
                {v} <span style={{ fontSize: 12, color: "var(--ph-text-muted)", fontWeight: 400 }}>{u}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Map with route + photo strip */}
      <div style={{ position: "relative", height: 220, background: "var(--ph-surface-muted)" }}>
        {/* Stylized map — abstract topo */}
        <svg viewBox="0 0 600 220" preserveAspectRatio="xMidYMid slice" style={{ width: "100%", height: "100%", display: "block" }}>
          {/* Topo lines */}
          <defs>
            <linearGradient id="mapBg" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor="color-mix(in oklch, var(--ph-data-3) 12%, var(--ph-surface))" />
              <stop offset="100%" stopColor="var(--ph-surface)" />
            </linearGradient>
          </defs>
          <rect width="600" height="220" fill="url(#mapBg)" />
          {[40, 80, 120, 160].map((y, i) => (
            <path key={i} d={`M -10 ${y} Q 100 ${y - 14} 220 ${y - 4} T 440 ${y + 6} T 620 ${y - 8}`}
                  stroke="var(--ph-divider)" strokeWidth="1" fill="none" opacity="0.6" />
          ))}
          {/* Route */}
          <path d="M 30 175 Q 80 130 130 145 T 220 110 Q 280 80 320 100 T 410 60 Q 470 50 510 90 T 560 60"
                stroke="var(--ph-data-3)" strokeWidth="4" fill="none" strokeLinecap="round" strokeLinejoin="round" />
          {/* Start */}
          <circle cx="30" cy="175" r="8" fill="var(--ph-surface)" stroke="var(--ph-data-3)" strokeWidth="3" />
          <text x="30" y="178" fontSize="10" fontWeight="700" textAnchor="middle" fill="var(--ph-data-3)">S</text>
          {/* PR pin */}
          <g transform="translate(320, 100)">
            <circle r="14" fill="var(--ph-primary)" />
            <circle r="14" fill="var(--ph-primary)" opacity="0.3">
              <animate attributeName="r" values="14;22;14" dur="2s" repeatCount="indefinite" />
              <animate attributeName="opacity" values="0.4;0;0.4" dur="2s" repeatCount="indefinite" />
            </circle>
            <text y="4" fontSize="11" fontWeight="700" textAnchor="middle" fill="#fff">PR</text>
          </g>
          {/* End */}
          <circle cx="560" cy="60" r="8" fill="var(--ph-surface)" stroke="var(--ph-data-3)" strokeWidth="3" />
          <text x="560" y="63" fontSize="9" fontWeight="700" textAnchor="middle" fill="var(--ph-data-3)">F</text>
        </svg>
        {/* Photo strip */}
        <div style={{ position: "absolute", bottom: 10, left: 10, display: "flex", gap: 6 }}>
          {[
            "linear-gradient(135deg, #6b8a4f, #3f5b34)",
            "linear-gradient(135deg, #c9a27a, #6b8a4f)",
            "linear-gradient(135deg, #4a7a8c, #2c4858)",
          ].map((bg, i) => (
            <div key={i} style={{
              width: 48, height: 48, borderRadius: 6,
              background: bg, border: "2px solid var(--ph-surface)",
              boxShadow: "0 2px 8px rgba(0,0,0,.15)",
            }} />
          ))}
        </div>
        {/* Distance scale */}
        <div style={{
          position: "absolute", bottom: 10, right: 10,
          padding: "4px 10px", background: "rgba(0,0,0,.5)", color: "#fff",
          borderRadius: 4, fontSize: 11, fontWeight: 500,
        }}>2 km</div>
      </div>

      {/* Splits table */}
      <div style={{ padding: 16, borderTop: "1px solid var(--ph-divider)" }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 10 }}>
          <div className="ph-label">{isNL ? "SPLITS · PER KM" : "SPLITS · PER KM"}</div>
          <Segmented value="pace" onChange={() => {}} options={[
            { value: "pace", label: "Pace" }, { value: "hr", label: "HR" },
          ]} />
        </div>
        <div style={{ display: "flex", flexDirection: "column", gap: 4 }}>
          {splits.map(s => {
            // bar width: faster = wider, mapped 60..100% of row
            const range = slowestPace - fastestPace || 1;
            const bw = 60 + (1 - (s.paceSec - fastestPace) / range) * 40;
            return (
              <div key={s.km} style={{ display: "grid", gridTemplateColumns: "24px 1fr 56px 44px", gap: 10, alignItems: "center" }}>
                <span className="ph-mono" style={{ fontSize: 12, color: "var(--ph-text-muted)" }}>{s.km}</span>
                <div style={{ height: 18, background: "var(--ph-surface-muted)", borderRadius: 4, position: "relative", overflow: "hidden" }}>
                  <div style={{
                    width: `${bw}%`, height: "100%",
                    background: s.fast ? "var(--ph-primary)" : "var(--ph-data-3)",
                    borderRadius: 4,
                  }} />
                  {s.fast && (
                    <span style={{ position: "absolute", right: 8, top: 1, fontSize: 10, fontWeight: 700, color: "#fff" }}>★</span>
                  )}
                </div>
                <span className="ph-mono" style={{ fontSize: 13, fontWeight: 600, textAlign: "right" }}>{fmtPace(s.paceSec)}</span>
                <span className="ph-mono" style={{ fontSize: 12, color: "var(--ph-text-muted)", textAlign: "right" }}>{s.hr}</span>
              </div>
            );
          })}
        </div>
      </div>

      {/* HR zones */}
      <div style={{ padding: 16, borderTop: "1px solid var(--ph-divider)" }}>
        <div className="ph-label" style={{ marginBottom: 10 }}>{isNL ? "HARTSLAGZONES" : "HEART RATE ZONES"}</div>
        <div style={{ display: "flex", height: 28, borderRadius: "var(--ph-r-sm)", overflow: "hidden", marginBottom: 8 }}>
          {zones.map(z => (
            <div key={z.z} style={{
              width: `${z.pct}%`, background: z.color, color: "#fff",
              display: "flex", alignItems: "center", justifyContent: "center",
              fontSize: 11, fontWeight: 600,
            }}>{z.pct >= 8 ? `${z.pct}%` : ""}</div>
          ))}
        </div>
        <div style={{ display: "flex", justifyContent: "space-between", fontSize: 11, color: "var(--ph-text-muted)" }}>
          {zones.map(z => (
            <span key={z.z} style={{ display: "flex", alignItems: "center", gap: 4 }}>
              <span style={{ width: 8, height: 8, borderRadius: 2, background: z.color }} />
              {z.z}
            </span>
          ))}
        </div>
      </div>

      {/* Segments / PRs */}
      <div style={{ padding: 16, borderTop: "1px solid var(--ph-divider)" }}>
        <div className="ph-label" style={{ marginBottom: 10 }}>{isNL ? "SEGMENTEN" : "SEGMENTS"}</div>
        <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
          {segments.map((seg, i) => (
            <div key={i} style={{
              display: "flex", alignItems: "center", gap: 12,
              padding: "8px 0", borderBottom: i < segments.length - 1 ? "1px solid var(--ph-divider)" : "none",
            }}>
              <Icon name={seg.isPr ? "trending" : "map"} size={14} color={seg.isPr ? "var(--ph-primary)" : "var(--ph-text-muted)"} />
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontSize: 13, fontWeight: 500, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{seg.name}</div>
                <div className="ph-caption" style={{ fontSize: 11 }}>
                  {seg.pr} <span style={{ color: seg.delta.startsWith("-") ? "var(--ph-primary)" : "var(--ph-text-faint)" }}>{seg.delta}</span>
                </div>
              </div>
              <span className="ph-mono" style={{ fontSize: 13, fontWeight: 600 }}>{seg.time}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Actions */}
      <div style={{ padding: "12px 16px", borderTop: "1px solid var(--ph-divider)", display: "flex", gap: 8, alignItems: "center" }}>
        <button style={{
          display: "flex", alignItems: "center", gap: 6,
          padding: "6px 12px", borderRadius: 999, background: "transparent",
          color: "var(--ph-text-muted)", border: "1px solid var(--ph-border)",
          fontSize: 13, fontWeight: 500, cursor: "pointer",
        }}>
          <Icon name="heart" size={14} /> 24
        </button>
        <button style={{
          display: "flex", alignItems: "center", gap: 6,
          padding: "6px 12px", borderRadius: 999, background: "transparent",
          color: "var(--ph-text-muted)", border: "1px solid var(--ph-border)",
          fontSize: 13, fontWeight: 500, cursor: "pointer",
        }}>
          <Icon name="map" size={14} /> 5
        </button>
        <div style={{ flex: 1 }} />
        <Tag>{isNL ? "Tempo run" : "Tempo run"}</Tag>
      </div>
    </div>
  );
};

/* ───────────── Strava-inspired: Weekly summary ─────────────
   "You vs you" — this week vs last week with totals + bar comparison */
const WeekStats = ({ isNL }) => {
  const days = isNL
    ? ["M", "D", "W", "D", "V", "Z", "Z"]
    : ["M", "T", "W", "T", "F", "S", "S"];
  // Two parallel weeks of run minutes
  const thisWeek = [42, 0, 65, 0, 38, 0, 72];
  const lastWeek = [38, 26, 52, 0, 41, 0, 58];
  const max = Math.max(...thisWeek, ...lastWeek, 1);

  return (
    <Card padding={18}>
      <SectionHead
        title={isNL ? "Deze week" : "This week"}
        subtitle={isNL ? "Jij vs vorige week" : "You vs last week"}
      />

      {/* Totals row */}
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 10, marginBottom: 16, paddingBottom: 14, borderBottom: "1px solid var(--ph-divider)" }}>
        {[
          [isNL ? "Afstand" : "Distance", "32.4", "km", "+18%"],
          [isNL ? "Tijd" : "Time", "3u 47m", "", "+12%"],
          [isNL ? "Hoogte" : "Elev", "184", "m", "+4%"],
        ].map(([k, v, u, d], i) => (
          <div key={i}>
            <div className="ph-caption" style={{ fontSize: 10 }}>{k}</div>
            <div style={{ fontSize: 18, fontWeight: 700, fontFamily: "var(--ph-font-mono)", marginTop: 2 }}>
              {v}{u && <span style={{ fontSize: 11, fontWeight: 400, color: "var(--ph-text-muted)", marginLeft: 2 }}>{u}</span>}
            </div>
            <div style={{ fontSize: 11, color: "var(--ph-primary)", fontWeight: 600 }}>↑ {d}</div>
          </div>
        ))}
      </div>

      {/* Day-by-day stacked bars */}
      <div className="ph-label" style={{ marginBottom: 10 }}>{isNL ? "PER DAG · MIN" : "PER DAY · MIN"}</div>
      <div style={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: 6, height: 90, alignItems: "flex-end" }}>
        {thisWeek.map((m, i) => {
          const lh = (lastWeek[i] / max) * 100;
          const th = (m / max) * 100;
          return (
            <div key={i} style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 4, height: "100%" }}>
              <div style={{ flex: 1, width: "100%", display: "flex", gap: 2, alignItems: "flex-end", justifyContent: "center" }}>
                <div title={`Last: ${lastWeek[i]}m`} style={{
                  width: 8, height: `${lh}%`,
                  background: "var(--ph-text-faint)", opacity: 0.4, borderRadius: "2px 2px 0 0",
                  transition: "height var(--ph-motion-slow) var(--ph-ease-emph)",
                }} />
                <div title={`This: ${m}m`} style={{
                  width: 8, height: `${th}%`,
                  background: m > 0 ? "var(--ph-primary)" : "transparent",
                  borderRadius: "2px 2px 0 0",
                  transition: "height var(--ph-motion-slow) var(--ph-ease-emph)",
                }} />
              </div>
              <div style={{ fontSize: 10, color: "var(--ph-text-muted)", fontWeight: 500 }}>{days[i]}</div>
            </div>
          );
        })}
      </div>

      {/* Legend */}
      <div style={{ display: "flex", gap: 14, marginTop: 12, fontSize: 11, color: "var(--ph-text-muted)" }}>
        <span style={{ display: "flex", alignItems: "center", gap: 6 }}>
          <span style={{ width: 8, height: 8, borderRadius: 2, background: "var(--ph-primary)" }} />
          {isNL ? "Deze week" : "This week"}
        </span>
        <span style={{ display: "flex", alignItems: "center", gap: 6 }}>
          <span style={{ width: 8, height: 8, borderRadius: 2, background: "var(--ph-text-faint)", opacity: 0.5 }} />
          {isNL ? "Vorige" : "Last"}
        </span>
      </div>
    </Card>
  );
};

/* ───────────── Strava-inspired: Local Legend / weekly leaderboard ─────────────
   Mini leaderboard among friends for a chosen segment / metric. */
const LocalLegend = ({ isNL }) => {
  const board = [
    { rank: 1, name: "Eva van Loon", initial: "EV", value: "12×", isYou: false, color: "var(--ph-data-3)" },
    { rank: 2, name: isNL ? "Jij" : "You", initial: "M", value: "9×", isYou: true, color: "var(--ph-primary)" },
    { rank: 3, name: "Bram Hofman", initial: "BH", value: "7×", isYou: false, color: "var(--ph-data-4)" },
    { rank: 4, name: "Lara Brun", initial: "LB", value: "4×", isYou: false, color: "var(--ph-data-5)" },
  ];

  return (
    <Card padding={18}>
      <SectionHead
        title={isNL ? "Local Legend" : "Local Legend"}
        subtitle={isNL ? "Vondelpark Loop · 90 dagen" : "Vondelpark Loop · 90 days"}
        action={<Tag tone="primary" icon="trending">3.2 km</Tag>}
      />

      <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
        {board.map(p => (
          <div key={p.rank} style={{
            display: "flex", alignItems: "center", gap: 12,
            padding: "8px 10px", borderRadius: "var(--ph-r-sm)",
            background: p.isYou ? "var(--ph-primary-soft)" : "transparent",
          }}>
            <div style={{
              width: 24, textAlign: "center",
              fontSize: 13, fontWeight: 700,
              color: p.rank === 1 ? "#c0a050" : p.isYou ? "var(--ph-primary)" : "var(--ph-text-muted)",
              fontFamily: "var(--ph-font-mono)",
            }}>{p.rank}</div>
            <div style={{
              width: 28, height: 28, borderRadius: "50%",
              background: p.color, color: "#fff",
              display: "flex", alignItems: "center", justifyContent: "center",
              fontSize: 11, fontWeight: 700,
            }}>{p.initial}</div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 13, fontWeight: p.isYou ? 600 : 500, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                {p.name}
                {p.rank === 1 && <span style={{ marginLeft: 6 }}>👑</span>}
              </div>
            </div>
            <span className="ph-mono" style={{ fontSize: 13, fontWeight: 600 }}>{p.value}</span>
          </div>
        ))}
      </div>

      <Button variant="ghost" full size="sm" iconRight="chevronRight" style={{ marginTop: 8 }}>
        {isNL ? "Bekijk segment" : "View segment"}
      </Button>
    </Card>
  );
};

/* ───────── MicrosBar — single collapsible micronutrient bar ─────────
   Compact: one stacked bar (food + supplements), worst gap callout,
   chevron to expand into the full per-nutrient detail grid. */
const MicrosBar = ({ micros, t, lang, D }) => {
  const [open, setOpen] = React.useState(false);
  // Aggregate stats
  const totals = micros.map(m => ({ ...m, total: m.food + m.supp }));
  const avg = Math.round(totals.reduce((s, m) => s + Math.min(m.total, 100), 0) / totals.length);
  const gaps = totals.filter(m => m.total < 100 && m.supp === 0);
  const worstGap = gaps.length ? gaps.sort((a, b) => a.total - b.total)[0] : null;
  // Avg food vs supp share for the summary bar (proportional)
  const avgFood = Math.round(totals.reduce((s, m) => s + Math.min(m.food, 100), 0) / totals.length);
  const avgSupp = Math.round(totals.reduce((s, m) => s + Math.max(0, Math.min(m.supp, 100 - m.food)), 0) / totals.length);

  return (
    <Card padding={D.pad}>
      {/* Collapsed header row — clickable */}
      <button
        onClick={() => setOpen(o => !o)}
        style={{
          all: "unset", cursor: "pointer", display: "block", width: "100%",
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ display: "flex", alignItems: "baseline", gap: 8 }}>
              <span style={{ fontSize: 14, fontWeight: 600 }}>{t("micros")}</span>
              <span className="ph-mono" style={{ fontSize: 12, color: "var(--ph-text-muted)" }}>{avg}% {lang === "nl" ? "gem." : "avg"}</span>
              {worstGap && (
                <span style={{ fontSize: 10, fontWeight: 600, padding: "2px 6px", borderRadius: 999, background: "color-mix(in oklch, var(--ph-warning, #c08a3e) 18%, transparent)", color: "var(--ph-warning, #c08a3e)", textTransform: "uppercase", letterSpacing: ".05em" }}>
                  {worstGap.label} · {worstGap.total}%
                </span>
              )}
            </div>
          </div>
          <Icon name="chevronRight" size={16} color="var(--ph-text-muted)" style={{ transform: open ? "rotate(90deg)" : "none", transition: "transform var(--ph-motion-base) var(--ph-ease-emph)" }} />
        </div>
        {/* Single stacked summary bar */}
        <div style={{ position: "relative", height: 8, background: "var(--ph-surface-muted)", borderRadius: 999, overflow: "hidden", marginTop: 10 }}>
          <div style={{ position: "absolute", inset: 0, display: "flex" }}>
            <div style={{ width: `${avgFood}%`, background: "var(--ph-primary)", transition: "width var(--ph-motion-slow) var(--ph-ease-emph)" }} />
            {avgSupp > 0 && (
              <div style={{ width: `${avgSupp}%`, background: "var(--ph-primary)", opacity: 0.45, backgroundImage: "repeating-linear-gradient(135deg, transparent 0 4px, rgba(0,0,0,.28) 4px 5px)" }} />
            )}
          </div>
          <div style={{ position: "absolute", left: "100%", top: -2, bottom: -2, width: 1, background: "var(--ph-text-muted)", opacity: 0.4, transform: "translateX(-1px)" }} />
        </div>
        <div className="ph-caption" style={{ marginTop: 6, fontSize: 11, display: "flex", justifyContent: "space-between" }}>
          <span>{avgFood}% {t("fromFood")}{avgSupp > 0 && <span> · +{avgSupp}% {t("fromSupp")}</span>}</span>
          <span>{gaps.length > 0 ? `${gaps.length} ${lang === "nl" ? "tekorten" : "gaps"}` : (lang === "nl" ? "alles op koers" : "all on track")}</span>
        </div>
      </button>

      {/* Expanded detail */}
      {open && (
        <div style={{ marginTop: 16, paddingTop: 16, borderTop: "1px solid var(--ph-divider)" }}>
          <div style={{ display: "flex", alignItems: "center", gap: 14, fontSize: 11, color: "var(--ph-text-muted)", marginBottom: 12 }}>
            <div style={{ display: "flex", alignItems: "center", gap: 6 }}>
              <span style={{ width: 10, height: 10, borderRadius: 2, background: "var(--ph-primary)" }} />
              {t("fromFood")}
            </div>
            <div style={{ display: "flex", alignItems: "center", gap: 6 }}>
              <span style={{ width: 10, height: 10, borderRadius: 2, background: "var(--ph-primary)", opacity: 0.45, backgroundImage: "repeating-linear-gradient(135deg, transparent 0 3px, rgba(0,0,0,.25) 3px 4px)" }} />
              {t("fromSupp")}
            </div>
            <div style={{ display: "flex", alignItems: "center", gap: 6 }}>
              <span style={{ width: 10, height: 10, borderRadius: 2, background: "var(--ph-warning, #c08a3e)", opacity: 0.6 }} />
              {t("gap")}
            </div>
          </div>
          <div data-grid-collapse style={{ display: "grid", gridTemplateColumns: "repeat(2, 1fr)", gap: "12px 28px" }}>
            {micros.map(m => {
              const total = m.food + m.supp;
              const isGap = total < 100 && m.supp === 0;
              const foodW = Math.min(m.food, 100);
              const suppW = Math.min(m.supp, 100 - foodW);
              return (
                <div key={m.key}>
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline", marginBottom: 6 }}>
                    <span style={{ fontSize: 13, fontWeight: 500, color: isGap ? "var(--ph-warning, #c08a3e)" : "var(--ph-text)" }}>
                      {m.label}
                    </span>
                    <span className="ph-mono" style={{ fontSize: 12, color: "var(--ph-text-muted)" }}>
                      <span style={{ color: "var(--ph-text)", fontWeight: 600 }}>{total}%</span>
                      <span style={{ color: "var(--ph-text-faint)" }}> · {m.target}{m.unit}</span>
                    </span>
                  </div>
                  <div style={{ position: "relative", height: 6, background: "var(--ph-surface-muted)", borderRadius: 999, overflow: "hidden" }}>
                    <div style={{ position: "absolute", inset: 0, display: "flex" }}>
                      <div style={{ width: `${foodW}%`, background: m.c }} />
                      {suppW > 0 && (
                        <div style={{ width: `${suppW}%`, background: m.c, opacity: 0.45, backgroundImage: "repeating-linear-gradient(135deg, transparent 0 4px, rgba(0,0,0,.28) 4px 5px)" }} />
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </Card>
  );
};

Object.assign(window, {
  StrengthScreen, FoodScreen, CommunityScreen, TabletRail, TabletToday, Foldable, MobileFrame,
  FeedCard, EventCard, ChallengeCard, FeaturedActivity, WeekStats, LocalLegend,
  MicrosBar,
  useT, fmtWeight, weightUnit, densityScale, STRINGS,
});
