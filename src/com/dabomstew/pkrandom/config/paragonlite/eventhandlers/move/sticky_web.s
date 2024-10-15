#DEFINE SP_Side 0x00
#DEFINE SP_ConditionType 0x04
#DEFINE SP_ConditionPtr 0x08
#DEFINE SP_MessageId 0x0C
#DEFINE SP_Work 0x10

    push    {r4-r7, lr}
    sub     sp, #0x1C
    mov     r5, r2
    mov     r6, r0
    mov     r7, r1
    str     r3, [sp, #SP_Work]
    
    bl      Battle::MakeIndefiniteCondition
    mov     r4, r0
    
    mov     r0, r5
    bl      Battle::GetSideFromOpposingPokeId
    str     r0, [sp, #SP_Side]
    
    mov     r0, #SC_StickyWeb
    str     r0, [sp, #SP_ConditionType]
    str     r4, [sp, #SP_ConditionPtr]
    ldr     r0, =TXT_StickyWeb_LaidOnTheGround
    str     r0, [sp, #SP_MessageId]
    
    ldr     r3, [sp, #SP_Work]
    
    mov     r0, r6
    mov     r1, r7
    mov     r2, r5
    bl      Battle::HandlerCommon_CreateSideStatus
    
Return:
    add     sp, #0x1C
    pop     {r4-r7, pc}