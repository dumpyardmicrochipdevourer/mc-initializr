import {test} from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import {translations} from '../../main/resources/static/i18n.js';

test('translations', () => {
  assert.deepEqual(Object.keys(translations.ru).sort(), Object.keys(translations.en).sort());
  const html = readFileSync(new URL('../../main/resources/static/index.html', import.meta.url), 'utf8');
  for (const dictionary of Object.values(translations)) {
    for (const value of Object.values(dictionary)) assert.ok(value.trim());
    for (const [, key] of html.matchAll(/data-t(?:-aria|-content)?="([^"]+)"/g)) assert.ok(dictionary[key], key);
  }
});
