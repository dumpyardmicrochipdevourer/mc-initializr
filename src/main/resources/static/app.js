import {translations, translate, pickLanguage} from './i18n.js';
import {restoreConfig, javaVersionFor, versionsFor} from './catalog.js';

const form = document.querySelector('#config');
const field = name => form.elements.namedItem(name);
const error = document.querySelector('#error');
const storeKey = 'mc-initializr-config-v1';
const languageKey = 'mc-initializr-lang';
let savedLanguage;
try { savedLanguage = localStorage.getItem(languageKey) || localStorage.getItem('start.lang'); } catch {}
let language = pickLanguage(savedLanguage, navigator.language);
const t = (key, ...args) => translate(language, key, ...args);
let previewFiles = {};
let currentFile = '';
let currentError = null;
let copyState = 'copy';
let catalog;
let busy = false;

function config() {
  return {
    name: field('name').value,
    engine: field('engine').value,
    version: field('version').value,
    memory: Number(field('memory').value),
    port: Number(field('port').value),
    players: Number(field('players').value),
    viewDistance: Number(field('viewDistance').value),
    gameMode: field('gameMode').value,
    difficulty: field('difficulty').value,
    motd: field('motd').value,
    whitelist: field('whitelist').checked,
    whitelistPlayers: field('whitelist').checked ? field('whitelistPlayers').value : '',
    backup: field('backups').checked ? field('backup').value : 'none',
    backupInterval: field('backupInterval').value,
    retention: field('backups').checked ? Number(field('retention').value) : catalog.defaults.retention,
    eula: field('eula').checked
  };
}

function applyLanguage() {
  document.documentElement.lang = language;
  for (const [attribute, target] of [['data-t', null], ['data-t-aria', 'aria-label'], ['data-t-content', 'content']]) {
    for (const node of document.querySelectorAll(`[${attribute}]`)) {
      const value = t(node.getAttribute(attribute));
      if (target) node.setAttribute(target, value);
      else node.textContent = value;
    }
  }
  for (const option of field('memory').options) option.textContent = t('memoryOption', option.value);
  for (const button of document.querySelectorAll('[data-lang]')) button.setAttribute('aria-pressed', String(button.dataset.lang === language));
  document.querySelector('#copy-file').textContent = t(copyState);
  if (currentError) renderError();
  if (catalog) update();
}

function update() {
  syncVersions();
  const c = config();
  document.querySelector('#backup-settings').hidden = c.backup === 'none';
  document.querySelector('#whitelist-settings').hidden = !c.whitelist;
  field('whitelistPlayers').required = c.whitelist;
  field('whitelistPlayers').disabled = !c.whitelist;
  field('retention').disabled = c.backup === 'none';
  document.querySelector('#zip-name').textContent = `${c.name || catalog.defaults.name}.zip`;
  const engine = catalog?.engines.find(item => item.id === c.engine)?.name || c.engine;
  document.querySelector('#selection').textContent = `${engine} · ${c.version} · ${t('memoryOption', c.memory)}`;
  document.querySelector('#selection-note').textContent = t(c.backup === 's3' ? 'backupS3' : c.backup === 'local' ? 'backupLocal' : 'backupNone');
  document.querySelector('#runtime-note').textContent = t('runtimeNote', javaVersionFor(catalog, c.version, c.engine), c.memory);
  document.querySelector('#backup-description').textContent = t(c.backup === 's3' ? 'backupS3Note' : 'backupLocalNote');
  try {
    const {eula, ...safe} = c;
    localStorage.setItem(storeKey, JSON.stringify(safe));
  } catch {}
}

function renderError() {
  const {key, fields} = currentError;
  error.textContent = fields.length ? fields.map(name => t(translations.en[`validation.${name}`] ? `validation.${name}` : 'error.invalid')).join(' ') : t(key);
}

function showError(key, fields = []) {
  currentError = {key, fields};
  renderError();
  error.hidden = false;
  error.scrollIntoView({block: 'nearest', behavior: 'smooth'});
}

function failure(key, fields = []) {
  return Object.assign(new Error(key), {translationKey: key, fields});
}

async function api(path, body) {
  const response = await fetch(path, {method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(body)});
  if (!response.ok) {
    let fields = [];
    try { fields = Object.keys((await response.json()).errors || {}); } catch { /* A proxy may return a non-JSON error. */ }
    throw failure(fields.length ? 'error.invalid' : 'error.generate', fields);
  }
  return response;
}

async function run(action) {
  if (busy || !form.reportValidity()) return;
  error.hidden = true;
  currentError = null;
  busy = true;
  for (const id of ['generate', 'preview']) document.getElementById(id).disabled = true;
  try { await action(); }
  catch (cause) { showError(cause.translationKey || 'error.network', cause.fields); }
  finally {
    busy = false;
    for (const id of ['generate', 'preview']) document.getElementById(id).disabled = !catalog;
  }
}

form.addEventListener('submit', event => {
  event.preventDefault();
  if (!field('eula').checked) {
    showError('error.eula');
    field('eula').focus();
    return;
  }
  run(async () => {
    const c = config();
    const response = await api('/api/generate', c);
    const blob = await response.blob();
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${c.name}.zip`;
    document.body.append(link);
    link.click();
    link.remove();
    setTimeout(() => URL.revokeObjectURL(url), 60000);
    document.querySelector('#download-dialog').showModal();
  });
});

function selectFile(name) {
  currentFile = name;
  document.querySelector('#current-file').textContent = name;
  document.querySelector('#file-content').textContent = previewFiles[name];
  copyState = 'copy';
  document.querySelector('#copy-file').textContent = t(copyState);
  for (const button of document.querySelectorAll('#file-list button')) button.setAttribute('aria-pressed', String(button.textContent === name));
}

document.querySelector('#preview').addEventListener('click', () => run(async () => {
  previewFiles = await (await api('/api/preview', config())).json();
  const list = document.querySelector('#file-list');
  list.replaceChildren();
  for (const name of Object.keys(previewFiles)) {
    const button = document.createElement('button');
    button.type = 'button';
    button.textContent = name;
    button.addEventListener('click', () => selectFile(name));
    list.append(button);
  }
  selectFile('compose.yaml');
  document.querySelector('#files-dialog').showModal();
}));

document.querySelector('#copy-file').addEventListener('click', async () => {
  try {
    await navigator.clipboard.writeText(previewFiles[currentFile]);
    copyState = 'copied';
  } catch { copyState = 'copyFallback'; }
  document.querySelector('#copy-file').textContent = t(copyState);
});
document.querySelector('#help').addEventListener('click', () => document.querySelector('#help-dialog').showModal());
for (const button of document.querySelectorAll('[data-close]')) button.addEventListener('click', () => button.closest('dialog').close());
for (const dialog of document.querySelectorAll('dialog')) dialog.addEventListener('click', event => {
  if (event.target === dialog) {
    const r = dialog.getBoundingClientRect();
    if (event.clientX < r.left || event.clientX > r.right || event.clientY < r.top || event.clientY > r.bottom) dialog.close();
  }
});

for (const button of document.querySelectorAll('[data-lang]')) button.addEventListener('click', () => {
  language = button.dataset.lang;
  applyLanguage();
  try { localStorage.setItem(languageKey, language); } catch {}
});

function syncVersions(selected = field('version').value) {
  const versions = versionsFor(catalog, field('engine').value);
  const current = Array.from(field('version').options, option => option.value);
  if (current.join() !== versions.join()) fillSelect('version', versions);
  field('version').value = versions.includes(selected) ? selected
    : versions.includes(catalog.defaults.version) ? catalog.defaults.version : versions[0];
}

function applyConfig(config) {
  field('engine').value = config.engine;
  syncVersions(config.version);
  for (const [key, value] of Object.entries(config)) {
    if (key === 'backup') continue;
    const input = field(key);
    if (!input) continue;
    if (input.type === 'checkbox') input.checked = value === true;
    else input.value = String(value);
  }
  field('backups').checked = config.backup !== 'none';
  field('backup').value = config.backup === 'none'
    ? catalog.backupDestinations.find(value => value !== 'none')
    : config.backup;
}

function fillSelect(name, values, translationKey = () => null) {
  field(name).replaceChildren(...values.map(value => {
    const option = new Option(value, value);
    const key = translationKey(value);
    if (key && translations.en[key]) option.dataset.t = key;
    return option;
  }));
}

document.querySelector('#reset').addEventListener('click', () => {
  form.reset();
  error.hidden = true;
  currentError = null;
  if (catalog) {
    applyConfig(catalog.defaults);
    update();
  }
});
form.addEventListener('input', () => { if (catalog) update(); });
form.addEventListener('change', () => { if (catalog) update(); });

async function initialize() {
  for (const id of ['generate', 'preview']) document.getElementById(id).disabled = true;
  applyLanguage();
  try {
    const response = await fetch('/api/catalog');
    if (!response.ok) throw failure('error.catalog');
    catalog = await response.json();
    document.querySelector('#engines').replaceChildren();
    for (const engine of catalog.engines) {
      const label = document.createElement('label');
      label.className = 'engine-option';
      const radio = document.createElement('input');
      Object.assign(radio, {type: 'radio', name: 'engine', value: engine.id, checked: engine.id === catalog.defaults.engine});
      const content = document.createElement('span');
      content.className = 'engine-body';
      const title = document.createElement('strong');
      title.textContent = engine.name;
      const description = document.createElement('small');
      const descriptionKey = `engine.${engine.id}`;
      if (translations.en[descriptionKey]) description.dataset.t = descriptionKey;
      else description.textContent = engine.description;
      content.append(title, description);
      label.append(radio, content);
      document.querySelector('#engines').append(label);
    }
    fillSelect('version', catalog.versions);
    fillSelect('memory', catalog.memoryOptions.map(String));
    fillSelect('backupInterval', catalog.backupIntervals, value => `interval${value}`);
    fillSelect('difficulty', catalog.difficulties, value => value);
    fillSelect('gameMode', catalog.gameModes, value => value);
    const destinations = document.querySelector('#backup-destinations');
    destinations.replaceChildren();
    for (const value of catalog.backupDestinations.filter(value => value !== 'none')) {
      const label = document.createElement('label');
      const input = document.createElement('input');
      Object.assign(input, {type: 'radio', name: 'backup', value});
      const text = document.createElement('span');
      if (translations.en[value]) text.dataset.t = value;
      else text.textContent = value;
      label.append(input, text);
      destinations.append(label);
    }
    let saved = null;
    try {
      saved = JSON.parse(localStorage.getItem(storeKey) || localStorage.getItem('pathos-start-config-v1') || 'null');
    } catch {}
    applyConfig(restoreConfig(catalog, saved));
    applyLanguage();
    for (const id of ['generate', 'preview']) document.getElementById(id).disabled = false;
  } catch (cause) { showError(cause.translationKey || 'error.catalog'); }
}
initialize();
