import {test} from 'node:test';
import assert from 'node:assert/strict';
import {restoreConfig, javaVersionFor} from '../../main/resources/static/catalog.js';

const catalog = {
  engines: [{id: 'PAPER'}], versions: ['1.21.11'],
  versionDetails: [{id: '1.21.11', javaVersion: 21}],
  memoryOptions: [4, 8], backupIntervals: ['24h'],
  backupDestinations: ['none', 'local', 's3'], difficulties: ['normal'], gameModes: ['survival'],
  defaults: {engine: 'PAPER', version: '1.21.11', memory: 4, backup: 'none', eula: false}
};

test('restore settings', () => {
  assert.deepEqual(restoreConfig(catalog, {memory: 8, backup: 's3'}),
    {...catalog.defaults, memory: 8, backup: 's3'});
  assert.equal(javaVersionFor(catalog, '1.21.11'), 21);
});

test('ignore stale settings and saved EULA', () => {
  assert.deepEqual(restoreConfig(catalog, {engine: 'removed', version: 'old', memory: 3, eula: true}),
    catalog.defaults);
});
