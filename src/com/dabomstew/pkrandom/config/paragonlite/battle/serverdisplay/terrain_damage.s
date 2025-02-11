; TODO    

#define S_fQueReserved 0x00
#define S_ReservedPos 0x04
#define S_pokeId 0x08
    
    push    {r4-r7, lr}
    sub     sp, #0x0C
    mov     r5, r0 ; ServerFlow
    mov     r6, r1 ; poke
    mov     r7, r2 ; terrain
    mov     r4, r3 ; damage
    
    mov     r0, r6
    bl      Battle::GetPokeId
    str     r0, [sp, #S_pokeId]
    
CheckPollutedTerrain:
    cmp     r7, #TERRAIN_Polluted
    bne     CheckHauntedTerrain
    
    
PollutedTerrain:
    ldr     r2, =BTLTXT_PollutedTerrain_Hurt
    b       ApplyTerrainDamage
    
    
CheckHauntedTerrain:
    cmp     r7, #TERRAIN_Haunted
    bne     OtherTerrain
    
    
HauntedTerrain:
    ldr     r2, =BTLTXT_HauntedTerrain_Hurt


ApplyTerrainDamage:
    ldr     r7, =ServerFlow.strParam
    add     r0, r5, r7
    mov     r1, #2
    bl      Battle::Handler_StrSetup
    
    add     r0, r5, r7
    ldr     r1, [sp, #S_pokeId]
    bl      Battle::Handler_AddArg
    
    b       CheckNoDamage


OtherTerrain:
    ldr     r0, =0x1AE4
    add     r0, r5
    bl      Battle::Handler_StrClear


CheckNoDamage:
    cmp     r4, #0 ; no damage
    ble     Return
    

CheckSimpleDamageEnabled:
    mov     r0, r5
    mov     r1, r6
    mov     r2, r4
    bl      Battle::ServerControl_CheckSimpleDamageEnabled
    cmp     r0, #FALSE
    beq     Return
    

ProcessDamage:
    ldr     r7, =ServerFlow.strParam
    
    mov     r0, r5
    add     r1, r5, r7
    bl      Battle::Handler_SetString
    
    add     r0, r5, r7
    bl      Battle::Handler_StrClear
    
    mov     r0, r5
    ldr     r1, [sp, #S_pokeId]
    bl      Battle::Handler_PokeIDToPokePos
    mov     r2, r0
    mov     r0, #0
    str     r0, [sp, #S_fQueReserved]
    str     r0, [sp, #S_ReservedPos]
    mov     r0, r5
    mov     r1, #(BTLANM_WeatherTerrainDamage >> 2)
    lsl     r1, #2
    mov     r3, #6 ; null pos
    bl      Battle::ServerControl_ViewEffect
    
    mov     r0, r6
    mov     r1, #BPV_CurrentHP
    bl      Battle::Poke_GetParam
    cmp     r0, r4
    bgt     SimpleDamage
    
    mov     r0, #0
    str     r0, [sp, #S_fQueReserved]
    str     r0, [sp, #S_ReservedPos]
    mov     r0, r5
    ldr     r1, =BTLANM_CameraInitOrthoImm
    mov     r2, #6 ; null pos
    mov     r3, #6 ; null pos
    bl      Battle::ServerControl_ViewEffect

SimpleDamage:
    mov     r0, r5
    mov     r1, r6
    mov     r2, r4
    mov     r3, #0
    bl      Battle::ServerControl_SimpleDamageCore

Return:
    add     sp, #0x0C
    pop     {r4-r7, pc}