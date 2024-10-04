#DEFINE VAR_00 0x00
#DEFINE VAR_01 0x04
#DEFINE VAR_02 0x08
#DEFINE VAR_03 0x0C
#DEFINE VAR_04 0x10
#DEFINE VAR_05 0x14
#DEFINE VAR_06 0x18 ; NEW
#DEFINE VAR_USE_ITEM_ID 0x1C
#DEFINE VAR_SUCCESS 0x20
#DEFINE VAR_EFFECTIVE 0x24
#DEFINE VAR_BATTLE_MON 0x28
#DEFINE VAR_18 0x2C
#DEFINE STACK_SIZE 0x30

    push    {r4-r7, lr}
    sub     sp, #STACK_SIZE
    mov     r4, r1
    ldr     r1, [r4, #HandlerParam_ChangeStatStage.header]
    mov     r5, r0
    lsl     r1, r1, #19
    str     r2, [sp, #VAR_USE_ITEM_ID]
    lsr     r1, r1, #27
    lsl     r1, r1, #24
    ldr     r0, [r5, #ServerFlow.pokeCon]
    lsr     r1, r1, #24
    bl      Battle::GetPokeParam
    str     r0, [sp, #VAR_BATTLE_MON]
    mov     r0, #0
    str     r0, [sp, #VAR_EFFECTIVE]
    mov     r0, #0
    str     r0, [sp, #VAR_SUCCESS]
    ldrb    r0, [r4, #HandlerParam_ChangeStatStage.pokeCount]
    mov     r6, #HandlerParam_ChangeStatStage.header
    cmp     r0, #0
    bls     Label_0x021ACE3C

Label_0x021ACDFC:
    add     r7, r4, r6
    ldr     r0, =ServerFlow.posPoke
    ldrb    r1, [r7, #HandlerParam_ChangeStatStage.pokeIds[0]]
    add     r0, r5, r0
    bl      BattleServer::PosPoke_IsExist
    cmp     r0, #0
    beq     Label_0x021ACE34
    ldrb    r1, [r7, #HandlerParam_ChangeStatStage.pokeIds[0]]
    ldr     r0, [r5, #ServerFlow.pokeCon]
    bl      Battle::GetPokeParam
    mov     r7, r0
    bl      Battle::IsPokeFainted
    cmp     r0, #0
    bne     Label_0x021ACE34
    mov     r2, #HandlerParam_ChangeStatStage.amount
    ldsb    r2, [r4, r2]
    ldr     r1, [r4, #HandlerParam_ChangeStatStage.stat]
    mov     r0, r7
    bl      Battle::IsStatChangeValid
    cmp     r0, #0
    beq     Label_0x021ACE34
    mov     r0, #1
    str     r0, [sp, #VAR_EFFECTIVE]
    b       Label_0x021ACE3C

Label_0x021ACE34:
    ldrb    r0, [r4, #HandlerParam_ChangeStatStage.pokeCount]
    add     r6, r6, #1
    cmp     r6, r0
    bcc     Label_0x021ACDFC

Label_0x021ACE3C:
    ldr     r0, [sp, #VAR_EFFECTIVE]
    cmp     r0, #0
    beq     Label_0x021ACE52
    ldr     r0, [r4, #HandlerParam_ChangeStatStage.header]
    lsl     r0, r0, #8
    lsr     r0, r0, #31
    beq     Label_0x021ACE52
    ldr     r1, [sp, #VAR_BATTLE_MON]
    mov     r0, r5
    bl      Battle::ServerDisplay_AbilityPopupAdd

Label_0x021ACE52:
    ldrb    r0, [r4, #HandlerParam_ChangeStatStage.pokeCount]
    mov     r6, #0
    cmp     r0, #0
    bls     Label_0x021ACED4
    mov     r0, r4
    str     r0, [sp, #VAR_18]
    add     r0, #24
    str     r0, [sp, #VAR_18]

Label_0x021ACE62:
    add     r7, r4, r6
    ldr     r0, =ServerFlow.posPoke
    ldrb    r1, [r7, #HandlerParam_ChangeStatStage.pokeIds[0]]
    add     r0, r5, r0
    bl      BattleServer::PosPoke_IsExist
    cmp     r0, #0
    beq     Label_0x021ACECC
    ldrb    r1, [r7, #HandlerParam_ChangeStatStage.pokeIds[0]]
    ldr     r0, [r5, #ServerFlow.pokeCon]
    bl      Battle::GetPokeParam
    mov     r7, r0
    bl      Battle::IsPokeFainted
    cmp     r0, #0
    bne     Label_0x021ACECC
    ldrb    r0, [r4, #HandlerParam_ChangeStatStage.pad2]
    cmp     r0, #0
    bne     Label_0x021ACE8E
    mov     r0, #1
    b       Label_0x021ACE90

Label_0x021ACE8E:
    mov     r0, #0

Label_0x021ACE90:
    mov     r1, #HandlerParam_ChangeStatStage.amount
    ldsb    r1, [r4, r1]
    ldr     r3, [r4, #HandlerParam_ChangeStatStage.stat]
    mov     r2, r7
    str     r1, [sp, #VAR_00]
    mov     r1, #31
    str     r1, [sp, #VAR_01]
    ldr     r1, [sp, #VAR_USE_ITEM_ID]
    str     r1, [sp, #VAR_02]
    ldr     r1, [r4, #HandlerParam_ChangeStatStage.pad1]
    str     r1, [sp, #VAR_03]
    ldrb    r1, [r4, #HandlerParam_ChangeStatStage.fMoveAnimation]
    str     r1, [sp, #VAR_04]
    ldr     r1, [r4, #HandlerParam_ChangeStatStage.header]
    str     r0, [sp, #VAR_05]
    
    ; NEW
    lsl     r0, r1, #5
    lsr     r0, #31
    str     r0, [sp, #VAR_06]
    ; ~NEW
    
    
    lsl     r1, r1, #19
    lsr     r1, r1, #27
    mov     r0, r5
    bl      Battle::ServerControl_StatStageChangeCore
    cmp     r0, #0
    beq     Label_0x021ACECC
    ldr     r1, [sp, #VAR_18]
    mov     r0, r5
    bl      Battle::Handler_SetString
    mov     r0, #1
    str     r0, [sp, #VAR_SUCCESS]

Label_0x021ACECC:
    ldrb    r0, [r4, #HandlerParam_ChangeStatStage.pokeCount]
    add     r6, r6, #1
    cmp     r6, r0
    bcc     Label_0x021ACE62

Label_0x021ACED4:
    ldr     r0, [sp, #VAR_EFFECTIVE]
    cmp     r0, #0
    beq     Label_0x021ACEEA
    ldr     r0, [r4, #HandlerParam_ChangeStatStage.header]
    lsl     r0, r0, #8
    lsr     r0, r0, #31
    beq     Label_0x021ACEEA
    ldr     r1, [sp, #VAR_BATTLE_MON]
    mov     r0, r5
    bl      Battle::ServerDisplay_AbilityPopupRemove

Label_0x021ACEEA:
    ldr     r0, [sp, #VAR_SUCCESS]
    add     sp, #STACK_SIZE
    pop     {r4-r7, pc}