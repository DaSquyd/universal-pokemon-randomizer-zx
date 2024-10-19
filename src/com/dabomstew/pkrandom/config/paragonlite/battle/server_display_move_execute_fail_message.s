#define TerminatorStr 0xFFFF0000

#define #ARG_01 0x00
#define #ARG_02 0x04
#define #ARG_03 0x08
#define #POKE 0x0C

    push    {r4-r7, lr}
    sub     sp, #0x10
    mov     r4, r0
    mov     r0, r1
    str     r1, [sp, #POKE]
    mov     r6, r2
    mov     r7, r3
    bl      Battle::GetPokeId
    mov     r5, r0
    cmp     r7, #MFC_NoStatusMoves
    bls     Switch
    b       Default

Switch:
    ; defaults to save on bytes
    ldr     r0, =TerminatorStr
    mov     r1, #SCMD_SetMessage
    
    #SWITCH r7
    #CASE Default
    #CASE NoPP
    #CASE Sleep
    #CASE Paralysis
    #CASE Freeze
    #CASE Default ; confusion
    #CASE Flinch
    #CASE FocusPunch
    #CASE Infatuation
    #CASE Disable
    #CASE Taunt
    #CASE Torment
    #CASE Imprison
    #CASE HealBlock
    #CASE MaxHP
    #CASE Insomnia
    #CASE Default ; Truant
    #CASE Default ; Move Lock
    #CASE Encore
    #CASE Return
    #CASE Gravity
    #CASE Default ; To Recover
    #CASE Ignore
    #CASE Ignore_FallAsleep
    #CASE Default ; Ignore Sleeping
    #CASE Return ; No Reaction
    #CASE Default ; Other
    #CASE NoStatusMoves

Sleep:
    str     r0, [sp, #ARG_01]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r2, #(309 - 255) ; "[poke] is fast asleep."
    add     r2, #255
    mov     r3, r5
    b       ReturnAddMessageImpl

Paralysis:
    str     r0, [sp, #ARG_01]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r2, #(276 >> 1) ; "[poke] is paralyzed! It can't move!"
    lsl     r2, #1
    mov     r3, r5
    b       ReturnAddMessageImpl

Freeze:
#if !(PARAGONLITE || REDUX)
    str     r0, [sp, #ARG_01]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r2, #(291 - 255) ; "[poke] is frozen solid!"
    add     r2, #255
    mov     r3, r5
    bl      Battle::ServerDisplay_AddMessageImpl
#endif
    b       Return

Flinch:
    mov     r2, #(363 - 255) ; "[poke] flinched and couldn't move!"
    add     r2, #255
    str     r0, [sp, #ARG_01]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r3, r5
    b       ReturnAddMessageImpl

FocusPunch:
    mov     r2, #(366 >> 1) ; "[poke] lost its focus and couldn't move!"
    lsl     r2, #1
    str     r0, [sp, #ARG_01]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r3, r5
    b       ReturnAddMessageImpl

Infatuation:
    str     r0, [sp, #ARG_01]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r2, #(336 >> 1)
    lsl     r2, #1 ; "[poke] is immobilized by love!"
    mov     r3, r5
    b       ReturnAddMessageImpl

Disable:
    str     r6, [sp, #ARG_01]
    str     r0, [sp, #ARG_02]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    ldr     r2, =595 ; "[poke]'s [move] is disabled!"
    mov     r3, r5
    b       ReturnAddMessageImpl

Encore:
    mov     r2, #(445 - 255) ; "[poke] is loafing around!"
    add     r2, #255
    str     r0, [sp, #ARG_01]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r3, r5
    b       ReturnAddMessageImpl

Taunt:
    str     r6, [sp, #ARG_01]
    str     r0, [sp, #ARG_02]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    ldr     r2, =571 ; "[poke] can't use [move] after the taunt!"
    mov     r3, r5
    b       ReturnAddMessageImpl

Torment:
    str     r6, [sp, #ARG_01]
    str     r0, [sp, #ARG_02]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r2, #(580 >> 2) ; "[poke] can't use the same move twice in a row due to the torment!"
    lsl     r2, #2
    mov     r3, r5
    b       ReturnAddMessageImpl

Imprison:
    mov     r3, r5 ; pokeId
    str     r6, [sp, #ARG_01] ; moveId
    str     r0, [sp, #ARG_02] ; terminator
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    ldr     r2, =589 ; "[poke] can't use the sealed [move]!"
    b       ReturnAddMessageImpl

HealBlock:
    mov     r3, r5 ; pokeId
    str     r6, [sp, #ARG_01] ; moveId
    str     r0, [sp, #ARG_02] ; terminator
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    ldr     r2, =890 ; "[poke] can't use [move] because of Heal Block!"
    b       ReturnAddMessageImpl
    
; NEW
NoStatusMoves:
    ; arg0 (r3) = pokeId
    ; arg1      = moveId
    ; arg2      = itemId
    ; arg3      = terminator
    
    str     r6, [sp, #ARG_01] ; moveId
    str     r0, [sp, #ARG_03] ; terminator
    
    ldr     r0, [sp, #POKE]
    bl      Battle::GetPokeHeldItem
    str     r0, [sp, #ARG_02] ; itemId
    
    ldr     r2, =BTLTXT_Common_StatusMovePreventItem_StatusMoveAttempted
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r3, r5 ; poke Id
    b       ReturnAddMessageImpl
; ~NEW

MaxHP:
    mov     r3, r5 ; pokeId
    str     r0, [sp, #ARG_01] ; terminator
    ldr     r2, =893 ; "[poke]'s HP is full!"
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    b       ReturnAddMessageImpl

Insomnia:
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r1, #SCMD_AbilityPopupIn
    mov     r2, r5
    bl      Battle::ServerDisplay_AddCommon
    
    mov     r3, r5 ; pokeId
    ldr     r0, =TerminatorStr
    str     r0, [sp, #ARG_01] ; terminator
    mov     r2, #(451 - 255) ; "[poke] stays awake!"
    add     r2, #255
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r1, #SCMD_SetMessage
    bl      Battle::ServerDisplay_AddMessageImpl
    
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r1, #SCMD_AbilityPopupOut
    mov     r2, r5
    bl      Battle::ServerDisplay_AddCommon
    
    b       Return

Gravity:
    str     r6, [sp, #ARG_01]
    str     r0, [sp, #ARG_02]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    ldr     r2, =1086 ; "[poke] can't use [move] because of gravity!"
    mov     r3, r5
    b       ReturnAddMessageImpl

Ignore:
    ldr     r0, [sp, #POKE]
    mov     r1, #MC_Sleep
    bl      Battle::CheckCondition
    cmp     r0, #0
    beq     Ignore_Sleeping
    
    ldr     r0, =TerminatorStr
    mov     r1, #SCMD_MessageStandard
    str     r0, [sp, #ARG_01]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r2, #194 ; "[poke] ignored orders while asleep!"
    mov     r3, r5
    b       ReturnAddMessageImpl

Ignore_Sleeping:
    mov     r0, #4
    bl      Battle::Random
    mov     r2, r0
    ldr     r0, =TerminatorStr
    mov     r1, #SCMD_MessageStandard
    str     r0, [sp, #ARG_01]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    add     r2, #188 ; "[poke] won't obey!", "[poke] is loafing around!", "[poke] turned away!", "[poke] pretended not to notice!"
    mov     r3, r5
    b       ReturnAddMessageImpl

Ignore_FallAsleep:
    mov     r1, #SCMD_MessageStandard
    str     r0, [sp, #ARG_01]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r2, #193 ; "[poke] began to nap!"
    mov     r3, r5
    b       ReturnAddMessageImpl

NoPP:
    ldr     r1, [sp, #POKE]
    mov     r0, r4
    mov     r2, r6
    bl      Battle::ServerDisplay_MoveMessage
    ldr     r0, =TerminatorStr
    mov     r1, #SCMD_MessageStandard
    str     r0, [sp, #ARG_01]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r2, #82 ; "But there was no PP left for the move!"
    mov     r3, r5
    b       ReturnAddMessageImpl

Default:
    mov     r1, #SCMD_MessageStandard
    str     r0, [sp, #ARG_01]
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r2, #71 ; "But it failed!"
    mov     r3, r5
    
ReturnAddMessageImpl:
    bl      Battle::ServerDisplay_AddMessageImpl

Return:
    add     sp, #0x10
    pop     {r4-r7, pc}