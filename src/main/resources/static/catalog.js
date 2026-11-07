export function javaVersionFor(catalog, version) {
  const details = catalog.versionDetails.find(item => item.id === version);
  if (!details) throw new Error(`Missing Java requirement for ${version}`);
  return details.javaVersion;
}

export function restoreConfig(catalog, saved) {
  const restored = {...catalog.defaults};
  if (!saved || typeof saved !== 'object' || Array.isArray(saved)) return restored;

  const choices = {
    engine: catalog.engines.map(engine => engine.id),
    version: catalog.versions,
    memory: catalog.memoryOptions,
    backupInterval: catalog.backupIntervals,
    backup: catalog.backupDestinations,
    difficulty: catalog.difficulties,
    gameMode: catalog.gameModes
  };
  for (const [key, fallback] of Object.entries(catalog.defaults)) {
    if (key === 'eula') continue;
    const value = saved[key];
    if (typeof value !== typeof fallback) continue;
    if (typeof value === 'number' && !Number.isFinite(value)) continue;
    if (choices[key] && !choices[key].includes(value)) continue;
    restored[key] = value;
  }
  return restored;
}
