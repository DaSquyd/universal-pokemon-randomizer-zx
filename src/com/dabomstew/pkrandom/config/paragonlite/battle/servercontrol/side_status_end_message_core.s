    push    {r3-r4, lr}
    mov     r3, r2
    cmp     r1, #(SIDE_STATUS_COUNT - 1)
    bhi     Return
    
    #switch r1
    #case Reflect
    #case LightScreen
    #case Safeguard
    #case Mist
    #case Tailwind
    #case LuckyChant
    #case Spikes
    #case ToxicSpikes
    #case StealthRock
    #case Return ; Wide Guard
    #case Return ; Quick Guard
    #case Rainbow
    #case SeaOfFire
    #case Swamp
    #case StickyWeb
    #case AuroraVeil
    
Reflect:
    mov     r2, #126
    b       DisplayMessage
    
LightScreen:
    mov     r2, #130
    b       DisplayMessage
    
Safeguard:
    mov     r2, #134
    b       DisplayMessage
    
Mist:
    mov     r2, #138
    b       DisplayMessage
    
Tailwind:
    mov     r2, #142
    b       DisplayMessage
    
LuckyChant:
    mov     r2, #146
    b       DisplayMessage
    
Spikes:
    mov     r2, #150
    b       DisplayMessage
    
ToxicSpikes:
    mov     r2, #154
    b       DisplayMessage
    
StealthRock:
    mov     r2, #158
    b       DisplayMessage
    
Rainbow:
    mov     r2, #166
    b       DisplayMessage
    
SeaOfFire:
    mov     r2, #170
    b       DisplayMessage
    
Swamp:
    mov     r2, #174
    b       DisplayMessage
    
StickyWeb:
#if BTLTXT_StickyWeb_Disappeared < 256
    mov     r2, #BTLTXT_StickyWeb_Disappeared
#else
    ldr     r2, =BTLTXT_StickyWeb_Disappeared
#endif
    b       DisplayMessage
    
AuroraVeil:
#if BTLTXT_AuroraVeil_Disappeared < 256
    mov     r2, #BTLTXT_AuroraVeil_Disappeared
#else
    ldr     r2, =BTLTXT_AuroraVeil_Disappeared
#endif

DisplayMessage:
    ldr     r4, =0xFFFF0000 ; null terminator 0xFFFF0000
    str     r4, [sp]
    
    ldr     r0, [r0, #0x0C]
    mov     r1, #SCMD_MessageStandard
    bl      Battle::ServerDisplay_AddMessageImpl
    
Return:
    pop     {r3-r4, pc}