/* Personal Health — core components, charts, and dashboard panels.
   All atoms read tokens from tokens.css. Inline styles via tokens for direct-edit safety.
   Exposed on window.* at bottom for cross-script use. */

const { useState, useMemo, useEffect, useRef } = React;

/* ───────── Icons (Lucide stand-in, 1.5 stroke) ───────── */
const Icon = ({ name, size = 20, color = "currentColor", strokeWidth = 1.6, style = {} }) => {
  const paths = {
    activity: "M22 12h-4l-3 9L9 3l-3 9H2",
    heart: "M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.29 1.51 4.04 3 5.5l7 7Z",
    moon: "M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79Z",
    flame: "M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5Z",
    footprints: "M4 16v-2.38c0-.4-.6-.5-.85-.85a3.5 3.5 0 1 1 5.7 0c-.25.35-.85.45-.85.85V16M16 16v-2.38c0-.4-.6-.5-.85-.85a3.5 3.5 0 1 1 5.7 0c-.25.35-.85.45-.85.85V16M2 21l3-3 3 3M14 21l3-3 3 3",
    droplet: "M12 22a7 7 0 0 0 7-7c0-2-1-3.9-3-5.5s-3.5-4-4-6.5c-.5 2.5-2 4.9-4 6.5S5 13 5 15a7 7 0 0 0 7 7Z",
    bike: "M5 17.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7ZM19 17.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7ZM12 17.5l-3-9h-3M9 8.5h6l3 6",
    waves: "M2 6c.6.5 1.2 1 2.5 1C7 7 7 5 9.5 5c2.6 0 2.4 2 5 2 2.5 0 2.5-2 5-2 1.3 0 1.9.5 2.5 1M2 12c.6.5 1.2 1 2.5 1 2.5 0 2.5-2 5-2 2.6 0 2.4 2 5 2 2.5 0 2.5-2 5-2 1.3 0 1.9.5 2.5 1M2 18c.6.5 1.2 1 2.5 1 2.5 0 2.5-2 5-2 2.6 0 2.4 2 5 2 2.5 0 2.5-2 5-2 1.3 0 1.9.5 2.5 1",
    dumbbell: "M14.4 14.4 9.6 9.6M18.657 21.485a2 2 0 1 1-2.829-2.828l-1.767 1.768a2 2 0 1 1-2.829-2.829l6.364-6.364a2 2 0 1 1 2.829 2.829l-1.768 1.767a2 2 0 1 1 2.828 2.829ZM2.929 4.929 4.343 3.515M21.485 18.657 22.9 17.243M5.343 5.343l13.314 13.314",
    leaf: "M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10ZM2 21c0-3 1.85-5.36 5.08-6",
    apple: "M12 10c-1-3-2-5-2-7 0-1 .5-2 2-2s2 1 2 2c0 2-1 4-2 7ZM7 22c-3 0-5-3-5-7 0-2 1-5 4-5s4 1 6 1 3-1 6-1 4 3 4 5c0 4-2 7-5 7-2 0-3-1-5-1s-3 1-5 1Z",
    target: "M22 12a10 10 0 1 1-20 0 10 10 0 0 1 20 0ZM18 12a6 6 0 1 1-12 0 6 6 0 0 1 12 0ZM14 12a2 2 0 1 1-4 0 2 2 0 0 1 4 0Z",
    trending: "M22 7l-9.5 9.5-5-5L2 17M16 7h6v6",
    bell: "M6 8a6 6 0 1 1 12 0c0 7 3 9 3 9H3s3-2 3-9M10.3 21a1.94 1.94 0 0 0 3.4 0",
    plus: "M12 5v14M5 12h14",
    chevronRight: "M9 18l6-6-6-6",
    check: "M20 6 9 17l-5-5",
    x: "M18 6 6 18M6 6l12 12",
    sun: "M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41M12 7a5 5 0 1 0 0 10 5 5 0 0 0 0-10Z",
    home: "M3 12l9-9 9 9M5 10v10h14V10",
    map: "M9 3l-7 3v15l7-3 7 3 7-3V3l-7 3-7-3ZM9 3v15M16 6v15",
    sparkles: "M12 3l1.9 4.6L18.5 9.5l-4.6 1.9L12 16l-1.9-4.6L5.5 9.5l4.6-1.9L12 3ZM19 14l1 2 2 1-2 1-1 2-1-2-2-1 2-1 1-2ZM5 14l.7 1.4L7 16l-1.3.6L5 18l-.7-1.4L3 16l1.3-.6L5 14Z",
    user: "M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8Z",
    users: "M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8ZM23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75",
    settings: "M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09a1.65 1.65 0 0 0-1-1.51 1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09a1.65 1.65 0 0 0 1.51-1 1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33h.01a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82v.01a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1Z",
    camera: "M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2zM12 17a4 4 0 1 0 0-8 4 4 0 0 0 0 8Z",
    image: "M21 15V5a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2zM8.5 10a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3ZM21 15l-5-5L5 21",
    sparkle: "M12 3l1.9 4.6L18.5 9.5l-4.6 1.9L12 16l-1.9-4.6L5.5 9.5l4.6-1.9L12 3Z",
    refresh: "M3 12a9 9 0 0 1 15-6.7L21 8M21 3v5h-5M21 12a9 9 0 0 1-15 6.7L3 16M3 21v-5h5",
  };
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none"
      stroke={color} strokeWidth={strokeWidth} strokeLinecap="round" strokeLinejoin="round"
      style={{ flexShrink: 0, ...style }}>
      <path d={paths[name] || ""} />
    </svg>
  );
};

/* ───────── Buttons ───────── */
const Button = ({ variant = "primary", size = "md", icon, iconRight, children, onClick, disabled, full, style = {} }) => {
  const sizes = {
    sm: { p: "8px 14px", fs: 13, ic: 14 },
    md: { p: "12px 18px", fs: 14, ic: 18 },
    lg: { p: "16px 22px", fs: 15, ic: 20 },
  };
  const s = sizes[size];
  const variants = {
    primary: { bg: "var(--ph-primary)", c: "var(--ph-text-on-primary)", b: "transparent" },
    secondary: { bg: "var(--ph-surface-muted)", c: "var(--ph-text)", b: "transparent" },
    ghost: { bg: "transparent", c: "var(--ph-text)", b: "transparent" },
    outline: { bg: "transparent", c: "var(--ph-text)", b: "var(--ph-border)" },
    danger: { bg: "var(--ph-danger)", c: "#fff", b: "transparent" },
  };
  const v = variants[variant];
  return (
    <button onClick={onClick} disabled={disabled} style={{
      display: "inline-flex", alignItems: "center", gap: 8, justifyContent: "center",
      padding: s.p, fontSize: s.fs, lineHeight: 1, fontWeight: 600,
      color: v.c, background: v.bg, border: `1px solid ${v.b}`,
      borderRadius: "var(--ph-r-pill)", cursor: disabled ? "not-allowed" : "pointer",
      opacity: disabled ? "var(--ph-opacity-disabled)" : 1, width: full ? "100%" : "auto",
      fontFamily: "var(--ph-font-sans)",
      transition: "background var(--ph-motion-fast) var(--ph-ease-standard), transform var(--ph-motion-fast)",
      ...style
    }}
      onMouseEnter={e => !disabled && (e.currentTarget.style.filter = "brightness(0.96)")}
      onMouseLeave={e => (e.currentTarget.style.filter = "none")}
    >
      {icon && <Icon name={icon} size={s.ic} />}
      {children}
      {iconRight && <Icon name={iconRight} size={s.ic} />}
    </button>
  );
};

const IconButton = ({ icon, onClick, label, size = 36, variant = "ghost" }) => {
  const bg = variant === "filled" ? "var(--ph-surface-muted)" : "transparent";
  return (
    <button aria-label={label} onClick={onClick} style={{
      width: size, height: size, borderRadius: "var(--ph-r-pill)",
      display: "inline-flex", alignItems: "center", justifyContent: "center",
      background: bg, border: "none", cursor: "pointer", color: "var(--ph-text)",
    }}>
      <Icon name={icon} size={18} />
    </button>
  );
};

/* ───────── Inputs ───────── */
const TextField = ({ label, value, onChange, placeholder, hint, error, suffix }) => (
  <label style={{ display: "flex", flexDirection: "column", gap: 6, fontFamily: "var(--ph-font-sans)" }}>
    {label && <span className="ph-label">{label}</span>}
    <div style={{
      display: "flex", alignItems: "center", gap: 8,
      padding: "10px 14px", borderRadius: "var(--ph-r-md)",
      background: "var(--ph-surface)",
      border: `1px solid ${error ? "var(--ph-danger)" : "var(--ph-border)"}`,
    }}>
      <input value={value || ""} onChange={e => onChange?.(e.target.value)} placeholder={placeholder}
        style={{ flex: 1, border: "none", outline: "none", background: "transparent", color: "var(--ph-text)", fontSize: 14, fontFamily: "inherit" }} />
      {suffix && <span style={{ color: "var(--ph-text-muted)", fontSize: 13 }}>{suffix}</span>}
    </div>
    {hint && !error && <span className="ph-caption">{hint}</span>}
    {error && <span className="ph-caption" style={{ color: "var(--ph-danger)" }}>{error}</span>}
  </label>
);

const Toggle = ({ on, onChange, label }) => (
  <label style={{ display: "inline-flex", alignItems: "center", gap: 10, cursor: "pointer" }}>
    <span onClick={() => onChange?.(!on)} style={{
      width: 38, height: 22, borderRadius: 999, padding: 2,
      background: on ? "var(--ph-primary)" : "var(--ph-surface-sunken)",
      transition: "background var(--ph-motion-fast)", display: "inline-block", position: "relative"
    }}>
      <span style={{
        width: 18, height: 18, borderRadius: 999, background: "#fff", display: "block",
        transform: on ? "translateX(16px)" : "translateX(0)",
        transition: "transform var(--ph-motion-fast) var(--ph-ease-standard)",
        boxShadow: "0 1px 2px rgba(0,0,0,.15)"
      }} />
    </span>
    {label && <span className="ph-body-sm">{label}</span>}
  </label>
);

/* ───────── Tags / chips / badges ───────── */
const Tag = ({ children, tone = "neutral", icon }) => {
  const tones = {
    neutral: { bg: "var(--ph-surface-muted)", c: "var(--ph-text)" },
    primary: { bg: "var(--ph-primary-soft)", c: "var(--ph-primary)" },
    success: { bg: "var(--ph-success-soft)", c: "var(--ph-success)" },
    warning: { bg: "var(--ph-warning-soft)", c: "var(--ph-warning)" },
    danger:  { bg: "var(--ph-danger-soft)", c: "var(--ph-danger)" },
    info:    { bg: "var(--ph-info-soft)", c: "var(--ph-info)" },
  };
  const t = tones[tone];
  return (
    <span style={{
      display: "inline-flex", alignItems: "center", gap: 6,
      padding: "4px 10px", borderRadius: "var(--ph-r-pill)",
      background: t.bg, color: t.c, fontSize: 12, fontWeight: 500, lineHeight: 1.2
    }}>
      {icon && <Icon name={icon} size={12} />}
      {children}
    </span>
  );
};

/* ───────── Card ───────── */
const Card = ({ children, padding = 20, style = {}, raised = false }) => (
  <div style={{
    background: "var(--ph-surface)", borderRadius: "var(--ph-r-lg)",
    padding, boxShadow: raised ? "var(--ph-elev-2)" : "var(--ph-elev-1)",
    ...style
  }}>{children}</div>
);

/* ───────── Segmented control ───────── */
const Segmented = ({ options, value, onChange }) => (
  <div style={{
    display: "inline-flex", padding: 4, borderRadius: "var(--ph-r-pill)",
    background: "var(--ph-surface-muted)"
  }}>
    {options.map(o => (
      <button key={o.value} onClick={() => onChange?.(o.value)} style={{
        padding: "6px 14px", border: "none", background: value === o.value ? "var(--ph-surface)" : "transparent",
        color: value === o.value ? "var(--ph-text)" : "var(--ph-text-muted)",
        borderRadius: "var(--ph-r-pill)", cursor: "pointer", fontSize: 13, fontWeight: 500,
        fontFamily: "var(--ph-font-sans)",
        boxShadow: value === o.value ? "var(--ph-elev-1)" : "none",
        transition: "all var(--ph-motion-fast)"
      }}>{o.label}</button>
    ))}
  </div>
);

/* ───────── Charts & data viz ───────── */

// Ring gauge (readiness, recovery, sleep score)
const RingGauge = ({ value = 76, max = 100, size = 160, stroke = 14, label, sublabel, color = "var(--ph-primary)", track = "var(--ph-surface-muted)" }) => {
  const r = (size - stroke) / 2;
  const c = 2 * Math.PI * r;
  const pct = Math.min(value / max, 1);
  return (
    <div style={{ position: "relative", width: size, height: size }}>
      <svg width={size} height={size}>
        <circle cx={size/2} cy={size/2} r={r} fill="none" stroke={track} strokeWidth={stroke} />
        <circle cx={size/2} cy={size/2} r={r} fill="none" stroke={color} strokeWidth={stroke}
          strokeDasharray={c} strokeDashoffset={c * (1 - pct)} strokeLinecap="round"
          transform={`rotate(-90 ${size/2} ${size/2})`}
          style={{ transition: "stroke-dashoffset var(--ph-motion-slow) var(--ph-ease-emph)" }} />
      </svg>
      <div style={{
        position: "absolute", inset: 0, display: "flex", flexDirection: "column",
        alignItems: "center", justifyContent: "center", gap: 2,
      }}>
        <div style={{ fontSize: size * 0.26, fontWeight: 600, color: "var(--ph-text)", letterSpacing: "-0.02em", lineHeight: 1, fontFeatureSettings: "'tnum' 1" }}>{value}</div>
        {label && <div className="ph-label" style={{ marginTop: 4 }}>{label}</div>}
        {sublabel && <div className="ph-caption">{sublabel}</div>}
      </div>
    </div>
  );
};

// Smooth area sparkline
const Sparkline = ({ data = [], width = 280, height = 64, color = "var(--ph-primary)", fill = "var(--ph-primary-soft)", showDots = false, baseline = true }) => {
  if (!data.length) return null;
  const max = Math.max(...data), min = Math.min(...data);
  const range = max - min || 1;
  const stepX = width / (data.length - 1 || 1);
  const pts = data.map((v, i) => [i * stepX, height - 8 - ((v - min) / range) * (height - 16)]);
  // smooth path
  const path = pts.reduce((acc, p, i) => {
    if (i === 0) return `M ${p[0]} ${p[1]}`;
    const prev = pts[i - 1];
    const cx1 = prev[0] + stepX / 2, cy1 = prev[1];
    const cx2 = p[0] - stepX / 2, cy2 = p[1];
    return `${acc} C ${cx1} ${cy1}, ${cx2} ${cy2}, ${p[0]} ${p[1]}`;
  }, "");
  const fillPath = `${path} L ${width} ${height} L 0 ${height} Z`;
  return (
    <svg width={width} height={height} viewBox={`0 0 ${width} ${height}`} style={{ overflow: "visible" }}>
      {baseline && <line x1="0" x2={width} y1={height-1} y2={height-1} stroke="var(--ph-divider)" strokeWidth="1" />}
      <path d={fillPath} fill={fill} opacity="0.6" />
      <path d={path} fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" />
      {showDots && pts.map(([x,y], i) => <circle key={i} cx={x} cy={y} r={i === pts.length-1 ? 4 : 2.5} fill={color} />)}
    </svg>
  );
};

// Bar chart (weekly volume)
const Bars = ({ data = [], width = 280, height = 100, color = "var(--ph-primary)", labelFor = i => "" }) => {
  const max = Math.max(...data, 1);
  const bw = (width - (data.length - 1) * 6) / data.length;
  return (
    <svg width={width} height={height + 20}>
      {data.map((v, i) => {
        const h = (v / max) * height;
        return (
          <g key={i}>
            <rect x={i * (bw + 6)} y={height - h} width={bw} height={h}
              rx="4" fill={i === data.length - 1 ? color : "var(--ph-accent)"} opacity={i === data.length - 1 ? 1 : 0.7} />
            <text x={i * (bw + 6) + bw/2} y={height + 14} textAnchor="middle" fontSize="10" fill="var(--ph-text-faint)" fontFamily="var(--ph-font-sans)">{labelFor(i)}</text>
          </g>
        );
      })}
    </svg>
  );
};

// Heatmap (consistency / muscle balance)
const Heatmap = ({ rows = 7, cols = 12, data, max = 1, gap = 4, cell = 14 }) => {
  const flat = data || Array.from({ length: rows * cols }, () => Math.random());
  return (
    <svg width={cols * (cell + gap)} height={rows * (cell + gap)}>
      {flat.map((v, i) => {
        const r = Math.floor(i / cols), c = i % cols;
        const t = Math.max(0, Math.min(1, v / max));
        return <rect key={i} x={c * (cell + gap)} y={r * (cell + gap)} width={cell} height={cell} rx="3"
          fill={`color-mix(in oklab, var(--ph-primary) ${t * 100}%, var(--ph-surface-muted))`} />;
      })}
    </svg>
  );
};

// HR zones bar
const ZoneBar = ({ zones = [10, 20, 30, 25, 15] }) => {
  const total = zones.reduce((a, b) => a + b, 0);
  return (
    <div>
      <div style={{ display: "flex", height: 10, borderRadius: 999, overflow: "hidden", background: "var(--ph-surface-muted)" }}>
        {zones.map((z, i) => (
          <div key={i} style={{
            width: `${(z / total) * 100}%`,
            background: `var(--ph-zone-${i+1})`
          }} />
        ))}
      </div>
      <div style={{ display: "flex", justifyContent: "space-between", marginTop: 6, color: "var(--ph-text-faint)", fontSize: 11 }}>
        {["Z1","Z2","Z3","Z4","Z5"].map(z => <span key={z}>{z}</span>)}
      </div>
    </div>
  );
};

// Muscle balance — body silhouette with weighted dots
const MuscleBalance = ({ groups }) => {
  // groups: [{name, x, y, load: 0..1}]
  const g = groups || [
    { name: "Chest",  x: 80, y: 70,  load: 0.65 },
    { name: "Back",   x: 80, y: 75,  load: 0.4 },
    { name: "Shoulders", x: 80, y: 50, load: 0.85 },
    { name: "Arms",   x: 50, y: 95,  load: 0.55 },
    { name: "Core",   x: 80, y: 110, load: 0.3 },
    { name: "Quads",  x: 70, y: 165, load: 0.7 },
    { name: "Hams",   x: 90, y: 170, load: 0.45 },
    { name: "Calves", x: 75, y: 215, load: 0.2 },
  ];
  return (
    <svg viewBox="0 0 160 250" style={{ width: "100%", maxWidth: 200, display: "block" }}>
      {/* simple silhouette */}
      <g fill="var(--ph-surface-muted)" stroke="var(--ph-border)" strokeWidth="1">
        <circle cx="80" cy="22" r="14" />
        <path d="M55 40 Q80 36 105 40 L112 90 Q108 110 100 130 L100 175 Q104 210 100 240 L86 240 L84 175 L76 175 L74 240 L60 240 Q56 210 60 175 L60 130 Q52 110 48 90 Z" />
      </g>
      {g.map((m, i) => (
        <g key={i}>
          <circle cx={m.x} cy={m.y} r={4 + m.load * 6}
            fill={`color-mix(in oklab, var(--ph-data-3) ${m.load * 100}%, var(--ph-data-2))`}
            opacity="0.95" />
        </g>
      ))}
    </svg>
  );
};

/* ───────── List rows ───────── */
const ListRow = ({ icon, title, meta, value, sub, accent }) => (
  <div style={{ display: "flex", alignItems: "center", gap: 14, padding: "12px 0" }}>
    <div style={{
      width: 40, height: 40, borderRadius: "var(--ph-r-md)",
      background: accent || "var(--ph-primary-soft)", color: "var(--ph-primary)",
      display: "flex", alignItems: "center", justifyContent: "center"
    }}>
      <Icon name={icon} size={18} />
    </div>
    <div style={{ flex: 1, minWidth: 0 }}>
      <div style={{ fontSize: 14, fontWeight: 500, color: "var(--ph-text)" }}>{title}</div>
      {meta && <div className="ph-caption">{meta}</div>}
    </div>
    {value && (
      <div style={{ textAlign: "right" }}>
        <div className="ph-mono" style={{ fontWeight: 600, fontSize: 14, color: "var(--ph-text)" }}>{value}</div>
        {sub && <div className="ph-caption">{sub}</div>}
      </div>
    )}
  </div>
);

/* ───────── Section header ───────── */
const SectionHead = ({ kicker, title, action }) => (
  <div style={{ display: "flex", alignItems: "flex-end", justifyContent: "space-between", marginBottom: 16 }}>
    <div>
      {kicker && <div className="ph-label" style={{ textTransform: "uppercase", letterSpacing: "0.08em", color: "var(--ph-primary)", marginBottom: 4 }}>{kicker}</div>}
      <div className="ph-h2">{title}</div>
    </div>
    {action}
  </div>
);

/* ───────── Triple ring (3 concentric rings — Apple-Activity style) ───────── */
const TripleRing = ({ rings = [], size = 180, gap = 6, stroke = 12, label, sublabel }) => {
  // rings: [{ value, max, color, key }]
  const cx = size / 2, cy = size / 2;
  return (
    <div style={{ position: "relative", width: size, height: size }}>
      <svg width={size} height={size}>
        {rings.map((r, i) => {
          const radius = (size - stroke) / 2 - i * (stroke + gap);
          if (radius <= 0) return null;
          const c = 2 * Math.PI * radius;
          const pct = Math.min((r.value || 0) / (r.max || 1), 1);
          return (
            <g key={r.key || i}>
              <circle cx={cx} cy={cy} r={radius} fill="none"
                stroke={`color-mix(in oklab, ${r.color} 16%, var(--ph-surface-muted))`} strokeWidth={stroke} />
              <circle cx={cx} cy={cy} r={radius} fill="none"
                stroke={r.color} strokeWidth={stroke}
                strokeDasharray={c} strokeDashoffset={c * (1 - pct)} strokeLinecap="round"
                transform={`rotate(-90 ${cx} ${cy})`}
                style={{ transition: "stroke-dashoffset var(--ph-motion-slow) var(--ph-ease-emph)" }} />
            </g>
          );
        })}
      </svg>
      <div style={{
        position: "absolute", inset: 0, display: "flex", flexDirection: "column",
        alignItems: "center", justifyContent: "center", gap: 2,
      }}>
        {label && <div className="ph-label" style={{ color: "var(--ph-text-muted)" }}>{label}</div>}
        {sublabel && <div className="ph-h2" style={{ margin: 0 }}>{sublabel}</div>}
      </div>
    </div>
  );
};

Object.assign(window, {
  Icon, Button, IconButton, TextField, Toggle, Tag, Card, Segmented,
  RingGauge, TripleRing, Sparkline, Bars, Heatmap, ZoneBar, MuscleBalance,
  ListRow, SectionHead
});
