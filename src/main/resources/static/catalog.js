export function versionsFor(catalog, engine) {
  return catalog.versionDetails.filter(item => Object.hasOwn(item.javaVersions, engine)).map(item => item.id);
}

export function javaVersionFor(catalog, version, engine) {
  const java = catalog.versionDetails.find(item => item.id === version)?.javaVersions[engine];
  if (!java) throw new Error(`Missing Java requirement for ${engine} ${version}`);
  return java;
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
  const versions = versionsFor(catalog, restored.engine);
  if (!versions.includes(restored.version)) {
    restored.version = versions.includes(catalog.defaults.version) ? catalog.defaults.version : versions[0];
  }
  const version = catalog.versionDetails.find(version => version.id === restored.version);
  if (!version.worldTypes.includes(restored.worldType)) restored.worldType = catalog.defaults.worldType;
  if (!version.hardcoreSupported) restored.hardcore = false;
  return restored;
}
