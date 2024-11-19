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
    
CheckGrassyTerrain:
    cmp     r7, #TERRAIN_Grassy
    beq     ApplyTerrainHealText
    
    
CheckPollutedTerrain:
    cmp     r7, #TERRAIN_Haunted
    beq     ApplyTerrainHealText
    

OtherTerrain:
    ldr     r0, =ServerFlow.strParam
    add     r0, r5
    bl      Battle::Handler_StrClear
    
    b       CheckNoHeal
    

ApplyTerrainHealText:
    ldr     r7, =ServerFlow.strParam
    add     r0, r5, r7
    mov     r1, #2
    ldr     r2, =BTLTXT_Common_HPRestored
    bl      Battle::Handler_StrSetup
    
    add     r0, r5, r7
    ldr     r1, [sp, #S_pokeId]
    bl      Battle::Handler_AddArg


CheckNoHeal:
    cmp     r4, #0 ; no healing
    ble     Return
    

ProcessHeal:
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
    ldr     r1, =BTLANM_Heal
    mov     r3, #6 ; null pos
    bl      Battle::ServerControl_ViewEffect
    
    mov     r0, r6
    mov     r1, #BPV_CurrentHP
    bl      Battle::GetPokeStat
    cmp     r0, r4
    bgt     SimpleHeal
    
    mov     r0, #0
    str     r0, [sp, #S_fQueReserved]
    str     r0, [sp, #S_ReservedPos]
    mov     r0, r5
    ldr     r1, =BTLANM_CameraInitOrthoImm
    mov     r2, #6 ; null pos
    mov     r3, #6 ; null pos
    bl      Battle::ServerControl_ViewEffect

SimpleHeal:
    mov     r0, r5
    mov     r1, r6
    mov     r2, r4
    bl      Battle::ServerControl_RecoverHPCore

Return:
    add     sp, #0x0C
    pop     {r4-r7, pc}