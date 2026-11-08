import {test} from 'node:test';
import assert from 'node:assert/strict';
import {restoreConfig, javaVersionFor, versionsFor} from '../../main/resources/static/catalog.js';

const catalog = {
  engines: [{id: 'PAPER'}], versions: ['1.21.11'],
  versionDetails: [{id: '1.21.11', javaVersions: {PAPER: 21}}],
  memoryOptions: [1, 1.5, 4, 8], backupIntervals: ['24h'],
  backupDestinations: ['none', 'local', 's3'], difficulties: ['normal'], gameModes: ['survival'],
  defaults: {engine: 'PAPER', version: '1.21.11', memory: 4, backup: 'none', eula: false}
};

test('restore settings', () => {
  assert.deepEqual(restoreConfig(catalog, {memory: 1.5, backup: 's3'}),
    {...catalog.defaults, memory: 1.5, backup: 's3'});
  assert.equal(javaVersionFor(catalog, '1.21.11', 'PAPER'), 21);
});

test('ignore stale settings and saved EULA', () => {
  assert.deepEqual(restoreConfig(catalog, {engine: 'removed', version: 'old', memory: 3, eula: true}),
    catalog.defaults);
});

test('engine compatibility and runtime', () => {
  const expanded = {...catalog,
    engines: [{id: 'PAPER'}, {id: 'VANILLA'}],
    versions: ['1.21.11', '1.16.5', '1.2.5'],
    versionDetails: [
      {id: '1.21.11', javaVersions: {PAPER: 21, VANILLA: 21}},
      {id: '1.16.5', javaVersions: {PAPER: 16, VANILLA: 8}},
      {id: '1.2.5', javaVersions: {VANILLA: 8}}
    ]};
  assert.deepEqual(versionsFor(expanded, 'PAPER'), ['1.21.11', '1.16.5']);
  assert.equal(javaVersionFor(expanded, '1.16.5', 'PAPER'), 16);
  assert.equal(javaVersionFor(expanded, '1.16.5', 'VANILLA'), 8);
  assert.equal(restoreConfig(expanded, {engine: 'PAPER', version: '1.2.5'}).version, '1.21.11');
  assert.equal(restoreConfig(expanded, {engine: 'VANILLA', version: '1.2.5'}).version, '1.2.5');
});
