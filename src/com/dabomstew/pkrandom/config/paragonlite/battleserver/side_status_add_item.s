#define S_Arg_SideId 0x00
#define S_Arg_HandlerTable 0x04
#define S_Arg_NumHandlers 0x08
#define S_SideId 0x0C
#define S_NumHandlers 0x10

#define Arg_R0_SideId 0x28
#define Arg_R1_StatusId 0x2C
#define Arg_R2_Condition 0x30

    push    {r0-r3}
    push    {r4-r7, lr}
    sub     sp, #0x14
    str     r0, [sp, #S_SideId]
    mov     r5, r1
    
    mov     r1, #(SIDE_STATUS_COUNT * SideStatus.size)
    mul     r1, r0
    ldr     r2, =BattleServer::SideStatuses
    mov     r0, #SideStatus.size
    mul     r0, r5
    add     r1, r2
    add     r4, r1, r0
    ldrh    r0, [r4, #SideStatus.level]
    
    ldr     r7, =BattleServer::Data_SideStatusEffectTable
    mov     r6, #0
    
Loop_Start:
    mov     r1, #0x0C ; data size
    mov     r3, r6
    mul     r3, r1
    ldr     r1, [r7, r3]
    add     r2, r7, r3
    cmp     r5, r1
    bne     Loop_End
    
    cmp     r0, #0
    bne     CheckMaxLevel
    
    add     r1, r7, #4
    add     r0, sp, #S_NumHandlers
    ldr     r1, [r1, r3]
    blx     r1
    
    ldr     r1, [sp, #S_SideId]
    mov     r2, #2 ; side default
    str     r1, [sp, #S_Arg_SideId]
    str     r0, [sp, #S_Arg_HandlerTable]
    ldr     r0, [sp, #S_NumHandlers]
    mov     r1, r5
    str     r0, [sp, #S_Arg_NumHandlers]
    mov     r0, #2 ; side status processing order
    mov     r3, #0 ; sub priority
    bl      Battle::Event_AddObject
    
    ldr     r2, [sp, #Arg_R2_Condition]
    mov     r1, #6 ; work idx
    mov     r5, r0
    bl      Battle::EventObject_SetWorkValue
    
    ldr     r0, [sp, #Arg_R2_Condition]
    str     r0, [r4, #SideStatus.condition]
    mov     r0, #0
    strh    r0, [r4, #SideStatus.turns]
    mov     r0, #1
    strh    r0, [r4, #SideStatus.level]
    
    add     sp, #0x14
    
    str     r5, [r4, #SideStatus.eventItem]
    mov     r0, r5
    pop     {r4-r7}
    pop     {r3}
    add     sp, #0x10
    bx      r3
    
CheckMaxLevel:
    ldr     r1, [r2, #0x08] ; max level
    cmp     r0, r1
    bcs     Loop_End
    
    ldrh    r0, [r4, #SideStatus.level]
    add     sp, #0x14
    add     r0, #1
    strh    r0, [r4, #SideStatus.level]
    ldr     r0, [r4, #SideStatus.eventItem]
    pop     {r4-r7}
    pop     {r3}
    add     sp, #0x10
    bx      r3
    
Loop_End:
    add     r6, #1
    cmp     r6, #SIDE_STATUS_COUNT
    bcc     Loop_Start
    
    mov     r0, #0
    add     sp, #0x14
    pop     {r4-r7}
    pop     {r3}
    add     sp, #0x10
    bx      r3
    