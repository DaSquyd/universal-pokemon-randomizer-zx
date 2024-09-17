    cmp     r0, #10
    bhi     ReturnNone
    
    #SWITCH r0
    #CASE ReturnNone
    #CASE ReturnNone ; Paralysis
    #CASE ReturnNone ; Sleep
    #CASE Freeze
    #CASE Burn
    #CASE Poison
    #CASE ReturnNone ; Confusion
    #CASE ReturnNone ; Attract
    #CASE ReturnNone ; Bind
    #CASE Nightmare
    #CASE Curse
    
Freeze:
    mov     r0, #((BTLTXT_Frostbite_Hurt + 1) >> 1)
    b       ReturnOddShift
    
Burn:
    mov     r0, #((BTLTXT_Burn_Hurt + 1) >> 1)
    b       ReturnOddShift
    
Poison:
    mov     r0, #BTLTXT_Poison_Hurt
    bx      lr
    
Nightmare:
    mov     r0, #(BTLTXT_Nightmare_Hurt >> 1)
    lsl     r0, #1
    bx      lr
    
Curse:
    mov     r0, #((BTLTXT_Curse_Hurt + 1) >> 3)
    lsl     r0, #2
    
ReturnOddShift:
    lsl     r0, #1
    sub     r0, #1
    bx      lr
    
ReturnNone:
    mov     r0, #0
    mvn     r0, r0 ; -1
    bx      lr