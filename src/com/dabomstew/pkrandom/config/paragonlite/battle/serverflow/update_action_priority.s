; r0: ServerFlow*
; r1: ActionOrderWork* startingAction
; r2: int numRemainingActions
; r3: int fProcessSpecialPriority

#define PUSH_STACK (4 * 5) ; r4-r7, lr

#define SP_NumRemainingActions 0x00
#define SP_MoveId 0x04
#define SP_ActionType 0x08
#define SP_Index 0x0C
#define SP_State1 0x10
#define SP_State2 0x14
#define SP_MovePriority 0x18
#define SP_ProcessSpecialPriority 0x1C
#define STACK_OFFSET (0x20 + PUSH_STACK)

    push    {r4-r7, lr}
    sub     sp, #(STACK_OFFSET - PUSH_STACK)
    mov     r4, r0
    mov     r5, r1
    str     r3, [sp, #SP_ProcessSpecialPriority]
    
    mov     r0, #0
    str     r0, [sp, #SP_Index]
    str     r2, [sp, #SP_NumRemainingActions]
    mov     r0, r2
    bne     Loop_Start
    b       Sort

Loop_Start:
    ldr     r0, [sp, #SP_Index]
    mov     r2, #1
    lsl     r7, r0, #4
    ldr     r1, [r5, r7]
    mov     r0, r4
    add     r6, r5, r7
    bl      Battle::ServerEvent_CalculateSpeed
    ldr     r1, =0x1FFF
    ldr     r2, [r6, #0x08]
    and     r0, r1
    ldr     r1, =0xFFFFE000
    and     r1, r2
    orr     r0, r1
    str     r0, [r6, #0x08]
    add     r0, r6, #4
    bl      Battle::BattleAction_GetActionType
    str     r0, [sp, #SP_ActionType]
    cmp     r0, #1
    bne     Loop_CheckSpecialPriority
    
    mov     r0, r6
    ldr     r0, [r0, #0x04]
    lsl     r1, r0, #28
    lsr     r1, r1, #28
    cmp     r1, #1
    bne     Loop_SetMoveIdZero
    
    lsl     r0, r0, #9
    lsr     r0, r0, #16
    b       Loop_MovePriority

Loop_SetMoveIdZero:
    mov     r0, #0

Loop_MovePriority:
    str     r0, [sp, #SP_MoveId]
    ldr     r0, =0x1D78
    add     r0, r4, r0
    bl      Battle::HEManager_PushState
    str     r0, [sp, #SP_State1]
    ldr     r1, [sp, #SP_MoveId]
    ldr     r2, [r5, r7]
    add     r6, r5, r7
    mov     r0, r4
    bl      Battle::ServerEvent_GetMovePriority
    lsl     r0, r0, #26
    lsr     r1, r0, #10
    ldr     r0, [r6, #0x08]
    ldr     r2, =0xFFC0FFFF
    and     r0, r2
    orr     r0, r1
    str     r0, [r6, #0x08]
    
    ldr     r0, =0x1D78
    ldr     r1, [sp, #SP_State1]
    add     r0, r4, r0
    bl      Battle::HEManager_PopState

Loop_CheckSpecialPriority:
    ldr     r0, [sp, #SP_ProcessSpecialPriority]
    cmp     r0, #0
    beq     Loop_CheckContinue
    
    ldr     r0, [sp, #SP_ActionType]
    cmp     r0, #BA_Fight
    beq     Loop_SpecialPriority
    cmp     r0, #BA_Shift
    bne     Loop_CheckContinue

Loop_SpecialPriority:
    bl      Battle::HEManager_PushState
    str     r0, [sp, #SP_State2]
    ldr     r1, [r5, r7]
    add     r6, r5, r7
    mov     r0, r4
    bl      Battle::ServerEvent_CheckSpecialPriority
    lsl     r0, r0, #29
    ldr     r2, [r6, #0x08]
    ldr     r1, =0xFFFF1FFF
    lsr     r0, r0, #16
    and     r1, r2
    orr     r0, r1
    str     r0, [r6, #0x08]
    ldr     r0, =0x1D78
    ldr     r1, [sp, #SP_State2]
    add     r0, r4, r0
    bl      Battle::HEManager_PopState

Loop_CheckContinue:
    ldr     r0, [sp, #SP_Index]
    add     r1, r0, #1
    ldr     r0, [sp, #SP_NumRemainingActions]
    str     r1, [sp, #SP_Index]
    cmp     r1, r0
    bcc     Loop_Start

Sort:
    ldr     r1, [sp, #SP_NumRemainingActions]
    mov     r0, r5
    bl      Battle::SortActionSub
    add     sp, #(STACK_OFFSET - PUSH_STACK)
    pop     {r4-r7, pc}