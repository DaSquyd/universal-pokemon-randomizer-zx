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
    mov     r0, #((BATTLE_TEXT_HURT_BY_FROSTBITE - 1) >> 1)
    b       ReturnOddShift
    
Burn:
    mov     r0, #((BATTLE_TEXT_HURT_BY_BURN - 1) >> 1)
    b       ReturnOddShift
    
Poison:
    mov     r0, #BATTLE_TEXT_HURT_BY_POISON
    bx      lr
    
Nightmare:
    mov     r0, #(BATTLE_TEXT_HURT_BY_NIGHTMARE >> 1)
    lsl     r0, #1
    bx      lr
    
Curse:
    mov     r0, #((BATTLE_TEXT_HURT_BY_CURSE - 1) >> 1)
    
ReturnOddShift:
    lsl     r0, #1
    add     r0, #1
    bx      lr
    
ReturnNone:
    mov     r0, #0
    mvn     r0, r0 ; -1
    bx      lr