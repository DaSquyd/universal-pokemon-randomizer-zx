#DEFINE PUSH_SIZE (4 * 5) ; r4-r7,lr

#DEFINE VAR_00 0x00
#DEFINE VAR_01 0x04
#DEFINE VAR_02 0x08 ; NEW
#DEFINE VAR_ATTACKING_MON 0x0C
#DEFINE VAR_F_MOVE_ANIMATION 0x10
#DEFINE VAR_STATE 0x14
#DEFINE STACK_SIZE (0x18 + PUSH_SIZE)

#DEFINE ARG_VOLUME (STACK_SIZE + 0x00)
#DEFINE ARG_ATTACKING_MON (STACK_SIZE + 0x04)
#DEFINE ARG_USE_ITEM_ID (STACK_SIZE + 0x08)
#DEFINE ARG_MOVE_SERIAL (STACK_SIZE + 0x0C)
#DEFINE ARG_F_MOVE_ANIMATION (STACK_SIZE + 0x10)
#DEFINE ARG_0A (STACK_SIZE + 0x14)
#DEFINE ARG_INTIMIDATE_FLAG (STACK_SIZE + 0x18)

    push    {r4-r7, lr}
    sub     sp, #(STACK_SIZE - PUSH_SIZE)
    mov     r7, r3
    str     r1, [sp, #VAR_ATTACKING_MON]
    ldr     r1, [sp, #ARG_USE_ITEM_ID]
    ldr     r6, [sp, #ARG_VOLUME]
    mov     r4, r2
    str     r1, [sp, #VAR_00] ; useItemId
    str     r6, [sp, #VAR_01] ; volume
    ldr     r3, [sp, #ARG_ATTACKING_MON]
    mov     r1, r4
    mov     r2, r7
    mov     r5, r0
    bl      Battle::ServerEvent_CheckSubstituteInteraction
    mov     r6, r0
    mov     r0, r4
    mov     r1, r7
    mov     r2, r6
    bl      Battle::IsStatChangeValid
    cmp     r0, #0
    bne     Label_0x021A6B4A
    ldr     r0, [sp, #ARG_F_MOVE_ANIMATION]
    cmp     r0, #0
    beq     Label_0x021A6B44
    mov     r0, r5
    mov     r1, r4
    mov     r2, r7
    mov     r3, r6
    bl      Battle::ServerDisplay_StatStageLimit
    ldr     r1, =0x078A
    mov     r0, #16
    ldrb    r2, [r5, r1]
    orr     r0, r2
    strb    r0, [r5, r1]

Label_0x021A6B44:
    add     sp, #(STACK_SIZE - PUSH_SIZE)
    mov     r0, #0
    pop     {r4-r7, pc}

Label_0x021A6B4A:
    mov     r0, r4
    bl      Battle::IsSubstituteActive
    cmp     r0, #0
    beq     Label_0x021A6B7C
    mov     r0, r4
    bl      Battle::GetPokeId
    mov     r3, r0
    ldr     r0, [sp, #VAR_ATTACKING_MON]
    cmp     r0, r3
    beq     Label_0x021A6B7C
    ldr     r0, [sp, #ARG_F_MOVE_ANIMATION]
    cmp     r0, #0
    beq     Label_0x021A6B76
    ldr     r0, =0xFFFF0000
    mov     r1, #91
    str     r0, [sp, #VAR_00]
    ldr     r0, [r5, #ServerFlow.serverCommandQueue]
    mov     r2, #210
    bl      Battle::ServerDisplay_AddMessageImpl

Label_0x021A6B76:
    add     sp, #(STACK_SIZE - PUSH_SIZE)
    mov     r0, #0
    pop     {r4-r7, pc}

Label_0x021A6B7C:
    str     r6, [sp, #VAR_00] ; volume
    mov     r0, #1
    str     r0, [sp, #VAR_F_MOVE_ANIMATION]
    ldr     r0, [sp, #ARG_MOVE_SERIAL]
    str     r0, [sp, #VAR_01] ; 
    ldr     r3, [sp, #VAR_ATTACKING_MON]
    
    ; NEW 
    ldr     r0, [sp, #ARG_INTIMIDATE_FLAG]
    str     r0, [sp, #VAR_02]
    ; ~NEW
    
    mov     r0, r5
    mov     r1, r4
    mov     r2, r7
    bl      Battle::ServerEvent_CheckStatChangeSuccess
    cmp     r0, #0
    beq     Label_0x021A6BD6
    ldr     r0, [sp, #ARG_USE_ITEM_ID]
    mov     r1, r4
    mov     r2, r7
    str     r0, [sp, #VAR_00]
    ldr     r0, [sp, #ARG_0A]
    mov     r3, r6
    str     r0, [sp, #VAR_01]
    mov     r0, r5
    bl      Battle::ServerDisplay_StatStage
    ldr     r0, =ServerFlow.heManager
    add     r0, r5, r0
    bl      Battle::HEManager_PushState
    str     r6, [sp, #VAR_00] ; volume
    str     r0, [sp, #VAR_STATE]
    ldr     r1, [sp, #VAR_ATTACKING_MON]
    
    ; NEW 
    ldr     r0, [sp, #ARG_INTIMIDATE_FLAG]
    str     r0, [sp, #VAR_01]
    ; ~NEW
    
    mov     r0, r5
    mov     r2, r4
    mov     r3, r7
    bl      Battle::ServerEvent_StatStageChangeApplied
    ldr     r0, =ServerFlow.heManager
    ldr     r1, [sp, #VAR_STATE]
    add     r0, r5, r0
    bl      Battle::HEManager_PopState
    b       Label_0x021A6C02

Label_0x021A6BD6:
    ldr     r0, [sp, #ARG_F_MOVE_ANIMATION]
    cmp     r0, #0
    beq     Label_0x021A6BFE
    ldr     r7, =ServerFlow.heManager
    add     r0, r5, r7
    bl      Battle::HEManager_PushState
    mov     r6, r0
    ldr     r2, [sp, #ARG_MOVE_SERIAL]
    mov     r0, r5
    mov     r1, r4
    
    ; NEW 
    ldr     r3, [sp, #ARG_INTIMIDATE_FLAG]
    ; ~NEW
    
    bl      Battle::ServerEvent_StatStageChangeFailed
    add     r0, r5, r7
    mov     r1, r6
    bl      Battle::HEManager_PopState

Label_0x021A6BFE:
    mov     r0, #0
    str     r0, [sp, #VAR_F_MOVE_ANIMATION]

Label_0x021A6C02:
    ldr     r0, [sp, #VAR_F_MOVE_ANIMATION]
    add     sp, #(STACK_SIZE - PUSH_SIZE)
    pop     {r4-r7, pc}