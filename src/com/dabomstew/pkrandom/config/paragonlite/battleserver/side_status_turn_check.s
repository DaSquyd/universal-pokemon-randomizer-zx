#define S_CallbackAddr 0x00
#define S_CallbackArgs 0x04
#define S_SideId 0x08

    push    {r3-r6, lr}
    sub     sp, #0x0C
    
    str     r0, [sp, #S_CallbackAddr]
    str     r1, [sp, #S_CallbackArgs]
    
    mov     r0, #0
    str     r0, [sp, #S_SideId]
    
SideLoop_Start:
    ldr     r0, [sp, #S_SideId]
    mov     r1, #(SIDE_STATUS_COUNT * SideStatus.size)
    
    mul     r1, r0
    ldr     r2, =BattleServer::SideStatuses
    mov     r4, #0 ; status id
    add     r6, r2, r1 ; r6 := side offset
    str     r0, [sp, #S_SideId]
    
StatusLoop_Start:
    mov     r0, #SideStatus.size
    mul     r0, r4
    add     r5, r6, r0
    ldrh    r0, [r5, #SideStatus.level]
    cmp     r0, #0
    beq     StatusLoop_End
    
    ldr     r0, [r5, #SideStatus.condition]
    lsl     r0, #29
    lsr     r0, #29 ; Get: 00000000_00000000_00000000_00000111
    cmp     r0, #2 ; turn condition
    bne     StatusLoop_End
    
    ldrh    r0, [r5, #SideStatus.turns]
    add     r0, #1
    strh    r0, [r5, #SideStatus.turns]
    
    ldr     r1, [r5, #SideStatus.condition]
    lsl     r1, #23
    lsr     r1, #26 ; Get: 00000000_00000000_00000001_11111000
    cmp     r0, r1
    bcc     StatusLoop_End
    
    mov     r0, #0
    strh    r0, [r5, #SideStatus.level]
    
    ldr     r0, [r5, #SideStatus.condition]
    mov     r1, #0x07
    bic     r0, r1 ; Mask: 11111111_11111111_11111111_11111000
    str     r0, [r5, #SideStatus.condition]
    
    ldr     r0, [r5, #SideStatus.eventItem]
    bl      Battle::EventObject_Remove
    
    ldr     r3, [sp, #S_CallbackAddr]
    mov     r0, #0
    str     r0, [r5, #SideStatus.eventItem]
    ldr     r0, [sp, #S_SideId]
    mov     r1, r4
    ldr     r2, [sp, #S_CallbackArgs]
    blx     r3
    
StatusLoop_End:
    add     r4, #1
    cmp     r4, #SIDE_STATUS_COUNT
    bcc     StatusLoop_Start
    
SideLoop_End:
    ldr     r0, [sp, #S_SideId]
    add     r0, #1
    str     r0, [sp, #S_SideId]
    cmp     r0, #2 ; number of sides
    bcc     SideLoop_Start
    add     sp, #0x0C
    pop     {r3-r6, pc}