    push    {r3-r7, lr}
    mov     r7, r1
    mov     r5, r0
    
    mov     r0, r7
    mov     r1, r2
    bl      Battle::PokeHasType
    mov     r6, r0
    
    mov     r4, #4
    lsl     r4, #10
    bl      Battle::EventVar_Push
    
    mov     r0, r7
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_GeneralUseFlag
    mov     r1, r6
    bl      Battle::EventVar_SetRewriteOnceValue
    
    mov     r0, r5
    mov     r1, #EVENT_GetIsSTAB
    bl      Battle::Event_CallHandlers
    
    mov     r0, #VAR_GeneralUseFlag
    bl      Battle::EventVar_GetValue
    mov     r6, r0
    bne     STAB
    
NotSTAB:
    ldr     r1, =0x1000
    b       CallRatioHandlers
    
STAB:
#if STAB_SINGLE_TYPE != STAB_MULTI_TYPE
    mov     r0, r7
    bl      Battle::GetPokeType
    bl      Battle::TypePair_IsMonoType
    cmp     r0, #FALSE
    beq     MultiType
    
;CheckExtraType:
;    mov     r0, r7 ; TODO
    
MonoType:
    ldr     r1, =(STAB_SINGLE_TYPE * 0x1000)
    b       CallRatioHandlers
    
MultiType:
#endif
    ldr     r1, =(STAB_MULTI_TYPE * 0x1000)
    
CallRatioHandlers:
    mov     r0, #VAR_Ratio
    bl      Battle::EventVar_SetValue

    mov     r0, #VAR_TypeMatchFlag
    mov     r1, r6
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, r5
    mov     r1, #EVENT_OnApplySTAB
    bl      Battle::Event_CallHandlers
    
    mov     r0, #VAR_Ratio
    bl      Battle::EventVar_GetValue
    mov     r4, r0
    
    bl      Battle::EventVar_Pop
    
    mov     r0, r4
    pop     {r3-r7, pc}