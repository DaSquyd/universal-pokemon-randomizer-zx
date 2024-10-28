; r0: attackType
; r1: defenderTypePair
; r2: defenderExtraType

#define S_Types 0x00
#define S_Types_1 0x00
#define S_Types_2 0x01
#define S_Types_3 0x02

    push    {r3-r6, lr}
    sub     sp, #0x04
    mov     r4, r1 ; defenderTypePair
    mov     r5, r0 ; attackType
    
    add     r0, sp, #S_Types
    strb    r2, [r0, #S_Types_3]
    
    mov     r0, r4
    add     r1, sp, #S_Types_1
    add     r2, sp, #S_Types
    add     r2, #S_Types_2
    add     r3, sp, #S_Types
    add     r3, #S_Types_3
    bl      Battle::TypePair_LoadTypes
    
Type1:
    mov     r0, r5 ; attackType
    add     r1, sp, #S_Types
    ldrb    r1, [r1, #S_Types_1]
    bl      Battle::GetTypeEffectiveness
    mov     r6, r0
    
Type2:
    mov     r0, r5 ; attackType
    add     r1, sp, #S_Types
    ldrb    r1, [r1, #S_Types_2]
    bl      Battle::GetTypeEffectiveness
    mov     r1, r6
    bl      Battle::MultiplyTypeEffectiveness
    mov     r6, r0
    
Type2:
    mov     r0, r5 ; attackType
    add     r1, sp, #S_Types
    ldrb    r1, [r1, #S_Types_3]
    bl      Battle::GetTypeEffectiveness
    mov     r1, r6
    bl      Battle::MultiplyTypeEffectiveness
    mov     r6, r0
    
    add     sp, #0x04
    pop     {r3-r6, pc}