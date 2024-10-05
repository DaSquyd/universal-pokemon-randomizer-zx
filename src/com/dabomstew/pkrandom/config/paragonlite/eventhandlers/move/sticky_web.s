#DEFINE OPPOSING_SIDE 0x00
#DEFINE CONDITION_TYPE 0x04
#DEFINE CONDITION_PTR 0x08
#DEFINE MESSAGE_ID 0x0C
#DEFINE VAR_20 0x10

    push    {r4-r7, lr}
    sub     sp, #0x1C
    mov     r5, r2
    mov     r6, r0
    mov     r7, r1
    str     r3, [sp, #VAR_20]
    bl      Battle::MakeIndefiniteCondition
    mov     r4, r0
    
    mov     r0, r5
    bl      Battle::GetSideFromOpposingPokeId
    str     r0, [sp, #OPPOSING_SIDE]
    
    mov     r0, #SC_StickyWeb
    str     r0, [sp, #CONDITION_TYPE]
    str     r4, [sp, #CONDITION_PTR]
    ldr     r0, =1215 ; message id
    str     r0, [sp, #MESSAGE_ID]
    
    ldr     r3, [sp, #VAR_20]
    
    mov     r0, r6
    mov     r1, r7
    mov     r2, r5
    bl      Battle::HandlerCommon_CreateSideStatus
    
Return:
    add     sp, #0x1C
    pop     {r4-r7, pc}