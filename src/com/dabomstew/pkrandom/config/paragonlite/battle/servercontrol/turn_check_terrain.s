#define S_fQueReserved 0x00
#define S_ReservedPos 0x04
#define S_PokeSet 0x08
#define S_fCameraReset 0x0C
#define STACK_SIZE 0x10

    push    {r4-r7, lr}
    sub     sp, #STACK_SIZE
    mov     r5, r0
    str     r1, [sp, #S_PokeSet]
    bl      Battle::Field_TurnCheckTerrain
    mov     r2, r0
    beq     Label_0x021A8842
    ldr     r0, [r5, #0x0C]
    mov     r1, #SCMD_TerrainEnd
    bl      Battle::ServerDisplay_AddCommon
    mov     r0, r5
    mov     r1, #0
    bl      Battle::ServerControl_PostChangeTerrain
    add     sp, #STACK_SIZE
    mov     r0, #FALSE
    pop     {r4-r7, pc}


Label_0x021A8842:
    mov     r0, #0
    str     r0, [sp, #S_fCameraReset]
    mov     r0, r5
    bl      Battle::ServerEvent_GetTerrain
    mov     r6, r0
    ldr     r0, [sp, #S_PokeSet]
    bl      BattleServer::PokeSet_SeekStart
    ldr     r0, [sp, #S_PokeSet]
    bl      BattleServer::PokeSet_SeekNext
    mov     r4, r0
    beq     CheckCameraReset


Loop_Start:
    mov     r0, r4
    bl      Battle::IsPokeFainted
    cmp     r0, #FALSE
    bne     Loop_CheckContinue
    
    
CheckSemiInvuln_Fly:
    mov     r0, r4
    mov     r1, #CF_Fly
    bl      Battle::Poke_GetConditionFlag
    cmp     r0, #FALSE
    bne     Loop_CheckContinue


CheckSemiInvuln_Dig:
    mov     r0, r4
    mov     r1, #CF_Dig
    bl      Battle::Poke_GetConditionFlag
    cmp     r0, #FALSE
    bne     Loop_CheckContinue
    
    
CheckSemiInvuln_Dive:
    mov     r0, r4
    mov     r1, #CF_Dive
    bl      Battle::Poke_GetConditionFlag
    cmp     r0, #FALSE
    bne     Loop_CheckContinue
    
    
CheckSemiInvuln_ShadowForce:
    mov     r0, r4
    mov     r1, #CF_ShadowForce
    bl      Battle::Poke_GetConditionFlag
    cmp     r0, #FALSE
    bne     Loop_CheckContinue
    
    
CheckFloating:
    mov     r0, r5
    mov     r1, r4
    mov     r2, #TRUE ; check for flying-type
    bl      Battle::ServerControl_CheckFloating
    cmp     r0, #FALSE
    bne     Loop_CheckContinue
    
    
DamageProcess_Push:
    ldr     r0, =ServerFlow.heManager
    add     r0, r5, r0
    bl      Battle::HEManager_PushState
    mov     r7, r0
    
    mov     r0, r5
    bl      Battle::ServerEvent_GetTerrain
    mov     r2, r0
    
    mov     r0, r4
    mov     r1, r6
    bl      Battle::Poke_CalcTerrainDamage
    mov     r3, r0
    mov     r0, r5
    mov     r1, r4
    mov     r2, r6
    bl      Battle::ServerEvent_CheckTerrainDamageReaction
    mov     r3, r0
    beq     DamageProcess_Pop
    
    mov     r0, r5
    mov     r1, r4
    mov     r2, r6
    bl      Battle::ServerDisplay_TerrainDamage
    
    mov     r0, #TRUE
    str     r0, [sp, #S_fCameraReset]


DamageProcess_Pop:
    ldr     r0, =ServerFlow.heManager
    add     r0, r5
    mov     r1, r7
    bl      Battle::HEManager_PopState


HealProcess_Push:
    ldr     r0, =ServerFlow.heManager
    add     r0, r5, r0
    bl      Battle::HEManager_PushState
    mov     r7, r0
    
    mov     r0, r5
    bl      Battle::ServerEvent_GetTerrain
    mov     r2, r0
    
    mov     r0, r4
    mov     r1, r6
    bl      Battle::Poke_CalcTerrainHeal
    mov     r3, r0
    mov     r0, r5
    mov     r1, r4
    mov     r2, r6
    bl      Battle::ServerEvent_CheckTerrainHealReaction
    mov     r3, r0
    beq     HealProcess_Pop
    
    mov     r0, r5
    mov     r1, r4
    mov     r2, r6
    bl      Battle::ServerDisplay_TerrainHeal
    
    mov     r0, #TRUE
    str     r0, [sp, #S_fCameraReset]


HealProcess_Pop:
    ldr     r0, =ServerFlow.heManager
    add     r0, r5
    mov     r1, r7
    bl      Battle::HEManager_PopState
    
    
Loop_CheckFainted:
    mov     r0, r5
    mov     r1, r4
    bl      Battle::ServerControl_CheckFainted


Loop_CheckContinue:
    ldr     r0, [sp, #S_PokeSet]
    bl      BattleServer::PokeSet_SeekNext
    mov     r4, r0
    bne     Loop_Start


CheckCameraReset:
    ldr     r0, [sp, #S_fCameraReset]
    cmp     r0, #FALSE
    beq     Return
    
    
ViewEffect:
    mov     r0, #0
    str     r0, [sp, #S_fQueReserved]
    str     r0, [sp, #S_ReservedPos]
    mov     r0, r5
    ldr     r1, =0x0255 ; BTLEFF_CAMERA_INIT_ORTHO_NO_WAIT
    mov     r2, #6
    mov     r3, #6
    bl      Battle::ServerControl_ViewEffect


Return:
    mov     r0, r5
    bl      Battle::ServerControl_CheckExpGet
    add     sp, #STACK_SIZE
    pop     {r4-r7, pc}