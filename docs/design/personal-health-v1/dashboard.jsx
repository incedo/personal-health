/* Personal Health — sample Today dashboard panels.
   Uses primitives + charts from components.jsx (on window). */

const { useState: useStateD } = React;

// ──────────────── Today hero (readiness + quick actions) ────────────────
const TodayHero = ({ simplify = false }) => (
  <Card padding={24} style={{ background: "linear-gradient(135deg, var(--ph-primary-soft) 0%, var(--ph-surface) 60%)" }}>
    <div style={{ display: "flex", flexWrap: "wrap", gap: 28, alignItems: "center" }}>
      <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 10 }}>
        <TripleRing
          size={170} stroke={11} gap={5}
          label="READINESS" sublabel="84"
          rings={[
            { key: "recovery", value: 84, max: 100, color: "var(--ph-primary)" },
            { key: "sleep",    value: 72, max: 100, color: "var(--ph-data-4)" },
            { key: "load",     value: 58, max: 100, color: "var(--ph-data-3)" },
          ]}
        />
        <div style={{ display: "flex", gap: 10, fontSize: 11 }}>
          <span style={{ display: "inline-flex", alignItems: "center", gap: 4, color: "var(--ph-primary)" }}><span style={{ width: 8, height: 8, borderRadius: 999, background: "var(--ph-primary)" }} />Recovery</span>
          <span style={{ display: "inline-flex", alignItems: "center", gap: 4, color: "var(--ph-data-4)" }}><span style={{ width: 8, height: 8, borderRadius: 999, background: "var(--ph-data-4)" }} />Sleep</span>
          <span style={{ display: "inline-flex", alignItems: "center", gap: 4, color: "var(--ph-data-3)" }}><span style={{ width: 8, height: 8, borderRadius: 999, background: "var(--ph-data-3)" }} />Load</span>
        </div>
      </div>
      <div style={{ flex: 1, minWidth: 220 }}>
        <div className="ph-label" style={{ textTransform: "uppercase", letterSpacing: "0.08em", color: "var(--ph-primary)" }}>Vrijdag · 1 mei</div>
        <div className="ph-h1" style={{ marginTop: 6 }}>Goedemorgen, Martijn</div>
        <div className="ph-body" style={{ color: "var(--ph-text-muted)", marginTop: 8, maxWidth: 460 }}>
          Je herstel staat sterk. Vandaag past een rustige duurloop of een lichte krachtsessie — en blijf weg van zware push-belasting.
        </div>
        {!simplify && (
          <div style={{ display: "flex", gap: 8, marginTop: 18, flexWrap: "wrap" }}>
            <Button icon="activity">Start run</Button>
            <Button variant="secondary" icon="dumbbell">Log workout</Button>
            <Button variant="ghost" icon="apple">Log meal</Button>
          </div>
        )}
        {simplify && (
          <div style={{ marginTop: 18 }}>
            <Button size="lg" icon="activity">Start vandaag</Button>
          </div>
        )}
      </div>
    </div>
  </Card>
);

// ──────────────── Vitals stack ────────────────
const Vital = ({ icon, label, value, unit, trend, data, tone = "primary" }) => (
  <Card padding={18}>
    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
      <div style={{
        width: 36, height: 36, borderRadius: "var(--ph-r-md)",
        background: `var(--ph-${tone}-soft)`, color: `var(--ph-${tone})`,
        display: "flex", alignItems: "center", justifyContent: "center"
      }}><Icon name={icon} size={18} /></div>
      {trend && <Tag tone={trend > 0 ? "success" : "neutral"}>{trend > 0 ? "+" : ""}{trend}%</Tag>}
    </div>
    <div className="ph-label" style={{ marginTop: 14 }}>{label}</div>
    <div style={{ display: "flex", alignItems: "baseline", gap: 4, marginTop: 2 }}>
      <span className="ph-metric" style={{ fontSize: 32, lineHeight: 1.1 }}>{value}</span>
      <span style={{ color: "var(--ph-text-muted)", fontSize: 13 }}>{unit}</span>
    </div>
    {data && <div style={{ marginTop: 10 }}><Sparkline data={data} width={240} height={40} color={`var(--ph-${tone})`} fill={`var(--ph-${tone}-soft)`} /></div>}
  </Card>
);

// ──────────────── Weekly volume ────────────────
const WeeklyVolume = () => {
  const days = ["M","T","W","T","F","S","S"];
  return (
    <Card>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
        <div>
          <div className="ph-label">DEZE WEEK</div>
          <div className="ph-h2" style={{ marginTop: 4 }}>5u 42m</div>
        </div>
        <Tag tone="primary" icon="trending">+12%</Tag>
      </div>
      <div style={{ marginTop: 16 }}>
        <Bars data={[42, 65, 0, 58, 30, 72, 90]} width={300} height={80} labelFor={i => days[i]} />
      </div>
      <div style={{ display: "flex", gap: 12, flexWrap: "wrap", marginTop: 12 }}>
        <Tag icon="footprints">11.4 km lopen</Tag>
        <Tag icon="bike">28 km fiets</Tag>
        <Tag icon="dumbbell">3 sessies kracht</Tag>
      </div>
    </Card>
  );
};

// ──────────────── Sleep panel ────────────────
const SleepPanel = () => (
  <Card>
    <div style={{ display: "flex", gap: 20, alignItems: "center" }}>
      <RingGauge value={88} size={120} stroke={11} label="SLAAP" sublabel="7u 42m" color="var(--ph-data-4)" />
      <div style={{ flex: 1 }}>
        <div className="ph-h3">Goed hersteld</div>
        <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 4 }}>Diep 1u 38m · REM 1u 52m</div>
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10, marginTop: 14 }}>
          <div>
            <div className="ph-caption">RHR</div>
            <div className="ph-mono" style={{ fontWeight: 600, fontSize: 16 }}>52 <span style={{ fontSize: 11, color: "var(--ph-text-muted)" }}>bpm</span></div>
          </div>
          <div>
            <div className="ph-caption">HRV</div>
            <div className="ph-mono" style={{ fontWeight: 600, fontSize: 16 }}>68 <span style={{ fontSize: 11, color: "var(--ph-text-muted)" }}>ms</span></div>
          </div>
        </div>
      </div>
    </div>
  </Card>
);

// ──────────────── Plan card ────────────────
const PlanCard = () => (
  <Card>
    <div style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 12 }}>
      <Icon name="sparkles" size={16} color="var(--ph-primary)" />
      <span className="ph-label" style={{ color: "var(--ph-primary)" }}>VANDAAG AANBEVOLEN</span>
    </div>
    <div className="ph-h3">Zone 2 duurloop · 45 min</div>
    <div className="ph-body-sm" style={{ color: "var(--ph-text-muted)", marginTop: 6 }}>
      Lage intensiteit past bij je herstel. Houd hartslag onder 142 bpm.
    </div>
    <div style={{ marginTop: 14 }}>
      <ZoneBar zones={[12, 65, 18, 4, 1]} />
    </div>
    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginTop: 18 }}>
      <div>
        <div className="ph-caption">Volgende: rust dag</div>
      </div>
      <Button size="sm" iconRight="chevronRight">Open plan</Button>
    </div>
  </Card>
);

// ──────────────── Muscle balance card ────────────────
const MuscleBalanceCard = () => (
  <Card>
    <SectionHead kicker="" title="Spier­balans" action={<Tag tone="warning">Schouders zwaar</Tag>} />
    <div style={{ display: "flex", gap: 16, alignItems: "center" }}>
      <MuscleBalance />
      <div style={{ flex: 1, display: "flex", flexDirection: "column", gap: 8 }}>
        {[
          ["Schouders", 0.85, "var(--ph-data-3)"],
          ["Quads", 0.7, "var(--ph-primary)"],
          ["Borst", 0.65, "var(--ph-primary)"],
          ["Rug", 0.4, "var(--ph-data-2)"],
          ["Core", 0.3, "var(--ph-data-2)"],
        ].map(([name, v, c]) => (
          <div key={name}>
            <div style={{ display: "flex", justifyContent: "space-between", fontSize: 12, color: "var(--ph-text-muted)" }}>
              <span>{name}</span><span>{Math.round(v * 100)}%</span>
            </div>
            <div style={{ height: 4, background: "var(--ph-surface-muted)", borderRadius: 999, marginTop: 4, overflow: "hidden" }}>
              <div style={{ height: "100%", width: `${v * 100}%`, background: c, borderRadius: 999 }} />
            </div>
          </div>
        ))}
      </div>
    </div>
  </Card>
);

// ──────────────── Body composition ────────────────
const BodyTrend = () => (
  <Card>
    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
      <div>
        <div className="ph-label">GEWICHT TREND · 30D</div>
        <div className="ph-metric" style={{ fontSize: 36, marginTop: 4 }}>78.4 <span style={{ fontSize: 14, color: "var(--ph-text-muted)" }}>kg</span></div>
        <div style={{ display: "flex", gap: 8, marginTop: 6 }}>
          <Tag tone="primary">−1.2 kg</Tag>
          <Tag>Vet 16.2%</Tag>
        </div>
      </div>
    </div>
    <div style={{ marginTop: 14 }}>
      <Sparkline data={[80.2, 80.0, 79.8, 79.9, 79.6, 79.4, 79.2, 79.0, 78.8, 78.9, 78.7, 78.5, 78.4]} width={420} height={80} showDots />
    </div>
  </Card>
);

// ──────────────── Recent sessions ────────────────
const RecentSessions = () => (
  <Card>
    <SectionHead title="Recent" action={<Button variant="ghost" size="sm" iconRight="chevronRight">Alle</Button>} />
    <ListRow icon="footprints" title="Ochtendloop" meta="Gisteren · 38 min · Z2" value="6.4 km" sub="6'04 / km" />
    <div style={{ height: 1, background: "var(--ph-divider)" }} />
    <ListRow icon="dumbbell" title="Krachttraining — push" meta="Eergisteren · 52 min" value="12,4 t" sub="volume" accent="var(--ph-warning-soft)" />
    <div style={{ height: 1, background: "var(--ph-divider)" }} />
    <ListRow icon="bike" title="Roadbike" meta="Zaterdag · 1u 24m" value="32.1 km" sub="24 km/u avg" accent="var(--ph-info-soft)" />
    <div style={{ height: 1, background: "var(--ph-divider)" }} />
    <ListRow icon="apple" title="Maaltijd · lunch" meta="Vandaag · 12:40" value="612 kcal" sub="42P · 58C · 22V" accent="var(--ph-success-soft)" />
  </Card>
);

// ──────────────── Consistency heatmap ────────────────
const ConsistencyCard = () => (
  <Card>
    <SectionHead kicker="" title="Consistentie · 12 weken" />
    <Heatmap rows={7} cols={12} max={1} />
    <div className="ph-caption" style={{ marginTop: 12 }}>Elke kolom = week. Elke rij = dag. Donkerder = meer beweging.</div>
  </Card>
);

Object.assign(window, {
  TodayHero, Vital, WeeklyVolume, SleepPanel, PlanCard,
  MuscleBalanceCard, BodyTrend, RecentSessions, ConsistencyCard
});
