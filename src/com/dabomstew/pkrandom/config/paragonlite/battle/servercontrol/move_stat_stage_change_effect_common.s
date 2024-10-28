#DEFINE PUSH_SIZE (4 * 5) ; r4-r7,lr

#DEFINE VAR_00 0x00
#DEFINE VAR_01 0x04
#DEFINE VAR_02 0x08
#DEFINE VAR_03 0x0C
#DEFINE VAR_04 0x10
#DEFINE VAR_05 0x14
#DEFINE VAR_06 0x18 ; new
#DEFINE VAR_38 0x1C
#DEFINE VAR_34 0x20
#DEFINE VAR_USE_ITEM_ID 0x24
#DEFINE VAR_MOVE_SERIAL 0x28
#DEFINE VAR_28 0x2C
#DEFINE VAR_24 0x30
#DEFINE VAR_20 0x34
#DEFINE VAR_1C 0x38
#DEFINE VAR_MOVE_EFFECT 0x3C
#DEFINE STACK_SIZE (0x40 + PUSH_SIZE)

#DEFINE ARG_00 (STACK_SIZE + 0x00)

    push    {r4-r7, lr}
    sub     sp, #(STACK_SIZE - PUSH_SIZE)
    mov     r6, r0
    ldr     r0, [sp, #ARG_00]
    str     r1, [sp, #VAR_38]
    str     r0, [sp, #ARG_00]
    mov     r0, #0
    str     r0, [sp, #VAR_20]
    mov     r0, r2
    str     r2, [sp, #VAR_34]
    mov     r7, r3
    bl      Battle::GetPokeId
    mov     r4, r0
    mov     r0, r6
    bl      Battle::WazaSerial_Inc
    str     r0, [sp, #VAR_MOVE_SERIAL]
    ldr     r0, [sp, #VAR_38]
    ldrh    r0, [r0, #0x00]
    bl      ARM9::GetMoveStatChangeStat
    str     r0, [sp, #VAR_24]
    mov     r0, #0
    str     r0, [sp, #VAR_28]
    ldr     r0, [sp, #VAR_24]
    cmp     r0, #0
    bls     Label_0x021A6A50

Label_0x021A698C:
    ldr     r1, [sp, #VAR_38]
    mov     r0, #0
    str     r0, [sp, #VAR_USE_ITEM_ID]
    str     r7, [sp, #VAR_00]
    add     r0, sp, #VAR_MOVE_EFFECT
    str     r0, [sp, #VAR_01]
    add     r0, sp, #VAR_1C
    str     r0, [sp, #VAR_02]
    ldrh    r1, [r1, #0x00]
    ldr     r2, [sp, #VAR_28]
    ldr     r3, [sp, #VAR_34]
    mov     r0, r6
    bl      Battle::ServerEvent_GetMoveStatChangeValue
    ldr     r0, [sp, #VAR_MOVE_EFFECT]
    cmp     r0, #0
    beq     Label_0x021A6A44
    cmp     r0, #10
    beq     Label_0x021A69D8
    ldr     r0, [sp, #VAR_1C]
    mov     r1, r4
    str     r0, [sp, #VAR_00]
    ldr     r0, [sp, #VAR_USE_ITEM_ID]
    str     r4, [sp, #VAR_01]
    str     r0, [sp, #VAR_02]
    ldr     r0, [sp, #VAR_MOVE_SERIAL]
    mov     r2, r7
    str     r0, [sp, #VAR_03]
    ldr     r0, [sp, #ARG_00]
    str     r0, [sp, #VAR_04]
    mov     r0, #1
    str     r0, [sp, #VAR_05]
    ldr     r3, [sp, #VAR_MOVE_EFFECT]
    
    ; NEW
    mov     r0, #0
    str     r0, [sp, #VAR_06]
    ; ~NEW
    
    mov     r0, r6
    bl      Battle::ServerControl_StatStageChangeCore
    str     r0, [sp, #VAR_USE_ITEM_ID]
    b       Label_0x021A6A0E

Label_0x021A69D8:
    mov     r5, #1

Label_0x021A69DA:
    ldr     r0, [sp, #VAR_1C]
    mov     r1, r4
    str     r0, [sp, #VAR_00]
    str     r4, [sp, #VAR_01]
    mov     r0, #0
    str     r0, [sp, #VAR_02]
    ldr     r0, [sp, #VAR_MOVE_SERIAL]
    mov     r2, r7
    str     r0, [sp, #VAR_03]
    ldr     r0, [sp, #ARG_00]
    mov     r3, r5
    str     r0, [sp, #VAR_04]
    mov     r0, #1
    str     r0, [sp, #VAR_05]
    
    ; NEW
    mov     r0, #0
    str     r0, [sp, #VAR_06]
    ; ~NEW
    
    mov     r0, r6
    bl      Battle::ServerControl_StatStageChangeCore
    cmp     r0, #0
    beq     Label_0x021A6A04
    mov     r0, #1
    str     r0, [sp, #VAR_USE_ITEM_ID]

Label_0x021A6A04:
    add     r0, r5, #1
    lsl     r0, r0, #24
    lsr     r5, r0, #24
    cmp     r5, #6
    bcc     Label_0x021A69DA

Label_0x021A6A0E:
    ldr     r0, [sp, #VAR_USE_ITEM_ID]
    cmp     r0, #0
    beq     Label_0x021A6A44
    ldr     r0, =ServerFlow.heManager
    add     r0, r6, r0
    bl      Battle::HEManager_PushState
    mov     r5, r0
    ldr     r0, [sp, #VAR_1C]
    ldr     r2, [sp, #VAR_38]
    str     r0, [sp, #VAR_00]
    ldrh    r2, [r2, #0x00]
    ldr     r3, [sp, #VAR_MOVE_EFFECT]
    mov     r0, r6
    mov     r1, r7
    bl      Battle::ServerEvent_MoveStatStageChangeApplied
    ldr     r0, =ServerFlow.heManager
    add     r0, r6, r0
    mov     r1, r5
    bl      Battle::HEManager_PopState
    mov     r0, #1
    str     r0, [sp, #VAR_20]

Label_0x021A6A44:
    ldr     r0, [sp, #VAR_28]
    add     r1, r0, #1
    ldr     r0, [sp, #VAR_24]
    str     r1, [sp, #VAR_28]
    cmp     r1, r0
    bcc     Label_0x021A698C

Label_0x021A6A50:
    ldr     r0, [sp, #VAR_20]
    add     sp, #(STACK_SIZE - PUSH_SIZE)
    pop     {r4-r7, pc}