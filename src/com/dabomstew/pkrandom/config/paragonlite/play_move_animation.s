#DEFINE PUSH_STACK_SIZE 0x14 ; r4-r7, lr
#DEFINE ADD_STACK_SIZE 0x14
#DEFINE STACK_SIZE (PUSH_STACK_SIZE + ADD_STACK_SIZE)

#DEFINE LINE_NUM (STACK_SIZE - 0x28)
#DEFINE PARAMS (STACK_SIZE - 0x24)

    push    {r4-r7, lr}
    sub     sp, #ADD_STACK_SIZE
    mov     r5, r0
    
    bl      BattleFx::ViewCmd_WaitEffect
    cmp     r0, #0
    bne     NoScript
    
    add     r4, sp, #PARAMS
    mov     r0, r4
    bl      BattleFx::ClearAnimParams
    
    ldr     r1, [r5, #0x10]
    add     r0, sp, #PARAMS
    strb    r1, [r0, #0x00]
    ldrb    r1, [r5, #0x0C]
    ldr     r6, =BattleFx::Data_Unk21F4280
    ldrh    r3, [r5, #0x00] ; anim id
    
    ; NEW - shifts the anim id of all moves beyond 559 up to a higher index as the intermediate indices are already in use
    mov     r2, #140
    lsl     r2, #2 ; 560
    cmp     r3, r2
    bcc     Continue
    
    mov     r2, #116 ; a/0/6/6 index 115
    add     r3, r2
    ; ~NEW
    
Continue:
    strb    r1, [r0, #0x01]
    ldrb    r1, [r5, #0x0D]
    ldr     r2, [r5, #0x08] ; a3
    strb    r1, [r0, #0x02]
    str     r4, [sp, #LINE_NUM] ; a5
    mov     r4, #0x62
    ldr     r0, [r6, #0x00]
    lsl     r4, #2 ; 392
    ldr     r0, [r0, r4] ; script_vm
    ldr     r1, [r5, #0x04] ; a2
    bl      BattleFx::VM_LoadScript
    
    ldr     r0, [r6, #0x00]
    mov     r1, #1
    add     r4, #0x68
    add     sp, #ADD_STACK_SIZE
    str     r1, [r0, r4]
    pop     {r4-r7, pc}

    
NoScript:
    mov     r6, #0xB7
    lsl     r6, #2 ; 0x02DC
    ldr     r7, =BattleFx::Data_Unk21F4280
    str     r6, [sp, #LINE_NUM] ; line num
    mov     r0, r6
    ldr     r1, [r7, #0x00]
    sub     r0, #0xE4
    ldrh    r0, [r1, r0] ; heap id
    ldr     r3, =BattleFx::Btlv_EffectC
    mov     r1, #0x14 ; size
    mov     r2, #1 ; calloc
    bl      ARM9::GFL_HeapAllocate
    mov     r4, r0
    
    add     r0, r6, #1
    str     r0, [sp, #LINE_NUM] ; line num
    ldr     r0, [r7, #0x00]
    sub     r6, #0xE4
    ldrh    r0, [r0, r6] ; heap id
    ldr     r3, =BattleFx::Btlv_EffectC
    mov     r1, #0x10 ; size
    mov     r2, #1 ; calloc
    bl      ARM9::GFL_HeapAllocate
    
    ldr     r1, [r5, #0x04]
    str     r0, [r4, #0x10]
    str     r1, [r4, #0x04]
    ldr     r1, [r5, #0x08]
    mov     r2, r4 ; data
    str     r1, [r4, #0x08]
    ldrh    r1, [r5, #0x00]
    mov     r3, #0 ; priority
    str     r1, [r4, #0x0C]
    ldr     r1, [r5, #0x10]
    strb    r1, [r0, #0x00]
    ldrb    r1, [r5, #0x0C]
    ldr     r0, [r4, #0x10]
    strb    r1, [r0, #0x01]
    ldrb    r1, [r5, #0x0D]
    ldr     r0, [r4, #0x10]
    strb    r1, [r0, #0x02]
    ldr     r0, [r7, #0x00]
    ldr     r1, =(BattleFx::Unk_21E0A18+1) ; callback func
    ldr     r0, [r0, #0x00] ; mgr
    bl      ARM9::GFL_TCBMgrAddTask
    
    ldr     r1, =(BattleFx::Unk_21E0A58+1) ; a2
    mov     r2, #0 ; a3
    bl      BattleFx::Unk_21E039C
    add     sp, #ADD_STACK_SIZE
    pop     {r4-r7, pc}
