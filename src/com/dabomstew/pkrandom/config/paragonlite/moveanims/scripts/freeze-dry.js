test('should strike Water-types super-effectively', () => {
    // Setup
    battle.noRandomDamage = true;
    battle.noCriticalHits = true;

    battle.parties = [
        [{species: 'jellicent', ability: 'damp', moves: ['gravity']}],
        [{species: 'dewgong', ability: 'hydration', moves: ['freeze-dry']}]
    ];

    // Act
    battle.select();

    // Assert
    const jellicent = battle.player.active[0];
    const expected_damage = game.calcDamage('dewgong', 'jellicent', 'freeze-dry', {stabMulti: 1.5, effectiveness: +1});
    const actual_damage = jellicent.maxHP - jellicent.currentHP;
    assert.equals(expected_damage, actual_damage);
});