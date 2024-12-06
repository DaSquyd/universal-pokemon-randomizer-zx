; THIS IS THE REPLACEMENT FUNCTION that handles move restriction checks. See is_selected_move_valid_ref.s!
; It's located in Overlay 167 at 0x021B47F8 for W2 and 0x021B47B8 for B2.
; References:
; W2            B2
; 0x021B3D7C    0x021B3D3C
; 0x021B3ECE    0x021B3E8E
; 0x021B4798    0x021B4758
; 0x021B4A9C    0x021B4A5C
; 0x021B5918    0x021B58D8
; It is never references by loading either as is or +1.

    push    {r4-r7, lr}
    add     sp, #-20
    mov     r5, r2
    mov     r7, r0
    mov     r6, r1
    mov     r4, r3
    
    cmp     r5, #165 ; Struggle
    bne     AssaultVestCheck
    
    add     sp, #20
    mov     r0, #0 ; false
    pop     {r4-r7, pc}
    

; NEW SECTION:
AssaultVestCheck:
    bl      Battle::CanPokeUseHeldItem
    cmp     r0, #FALSE
    beq     CheckChoiceItem
    
    ; Assault Vest
    mov     r0, r6
    bl      Battle::Poke_GetHeldItem
    mov     r1, #(640 >> 2) ; Assault Vest
    lsl     r1, #2
    cmp     r0, r1
#IF PARAGONLITE
    beq     NoStatusMove
    ; Protector
    lsr     r1, #1
    add     r1, #(321 - (640 >> 1)) ; Protector
    cmp     r0, r1
#ENDIF
    bne     CheckChoiceItem
    
NoStatusMove:
    mov     r0, r5
    bl      ARM9::IsMoveDamaging
    cmp     r0, #0
    bne     CheckChoiceItem
    
    cmp     r4, #0
    beq     NoStatusMove_ReturnTrue
    
    mov     r0, r4
    ldr     r2, =BTLTXT_Common_StatusMovePreventItem_StatusMoveSelected
    mov     r1, #1
    bl      Battle::DisplayMessage
    
    mov     r0, r6
    bl      Battle::Poke_GetHeldItem
    
    mov     r1, r0
    mov     r0, r4
    bl      Battle::StringParam_AddArg
    
NoStatusMove_ReturnTrue:
    add     sp, #20
    mov     r0, #1
    pop     {r4-r7, pc}
    



; Is the user holding Choice Band/Specs/Scarf and is already locked into a move?
CheckChoiceItem:
    bl      Battle::CanPokeUseHeldItem
    cmp     r0, #FALSE
    beq     CheckEncore
    
    mov     r0, r6
    mov     r1, #MC_ChoiceLock
    bl      Battle::CheckCondition
    cmp     r0, #FALSE
    beq     CheckEncore
    
    mov     r0, r6
    mov     r1, #MC_ChoiceLock
    bl      Battle::Poke_GetConditionData
    bl      Battle::ConditionPtr_GetMove
    str     r0, [sp, #0]
    
    ldr     r1, [sp, #0]
    mov     r0, r6
    bl      Battle::HasMove
    cmp     r0, #FALSE
    beq     CheckEncore
    
    ldr     r0, [sp, #0]
    cmp     r0, r5
    beq     CheckEncore
    
    cmp     r4, #0
    beq     ChoiceItemReturnTrue
    
    mov     r0, r4
    mov     r1, #1 ; File 0x11
    mov     r2, #99 ; "The [item] allows the use of only [move]"
    bl      Battle::DisplayMessage
    
    mov     r0, r6
    bl      Battle::Poke_GetHeldItem
    
    mov     r1, r0
    mov     r0, r4
    bl      Battle::StringParam_AddArg
    
    ldr     r1, [sp, #0]
    mov     r0, r4
    bl      Battle::StringParam_AddArg
    
ChoiceItemReturnTrue:
    add     sp, #20
    mov     r0, #TRUE
    pop     {r4-r7, pc}
    


; Is the user locked into a specific move due to Encore?
CheckEncore:
    mov     r0, r6
    mov     r1, #23 ; Encore
    bl      Battle::CheckCondition
    cmp     r0, #0
    beq     CheckTaunt
    
    mov     r0, r6
    mov     r1, #23 ; Encore
    bl      Battle::Poke_GetConditionData
    bl      Battle::ConditionPtr_GetMove
    str     r0, [sp, #4]
    cmp     r5, r0
    beq     CheckTaunt
    
    cmp     r4, #0
    beq     EncoreReturnTrue
    
    mov     r0, r4
    mov     r1, #1 ; File 0x11
    mov     r2, #100 ; "[Pokémon] can use only [move]!"
    bl      Battle::DisplayMessage
    
    mov     r0, r6
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, r4
    bl      Battle::StringParam_AddArg
    
    ldr     r1, [sp, #4]
    mov     r0, r4
    bl      Battle::StringParam_AddArg
    
EncoreReturnTrue:
    add     sp, #20
    mov     r0, #1
    pop     {r4-r7, pc}
    
    

; Is the user locked out of status moves due to Taunt?
CheckTaunt:
    mov     r0, r6
    mov     r1, #11 ; Taunt
    bl      Battle::CheckCondition
    cmp     r0, #0
    beq     CheckTorment
    
    mov     r0, r5
    bl      ARM9::IsMoveDamaging
    cmp     r0, #0
    bne     CheckTorment
    
    cmp     r4, #0
    beq     TauntReturnTrue
    
    ldr     r2, =571 ; "[Pokémon] can't use [move] after the taunt!"
    mov     r0, r4
    mov     r1, #2 ; File 0x12
    bl      Battle::DisplayMessage
    
    mov     r0, r6
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, r4
    bl      Battle::StringParam_AddArg
    
    mov     r0, r4
    mov     r1, r5
    bl      Battle::StringParam_AddArg
    
TauntReturnTrue:
    add     sp, #20
    mov     r0, #1
    pop     {r4-r7, pc}
    
    
    
; Is the user locked into using a different move due to Torment?
CheckTorment:
    mov     r0, r6
    mov     r1, #12 ; Torment
    bl      Battle::CheckCondition
    cmp     r0, #0
    beq     CheckDisable
    
    mov     r0, r6
    bl      Battle::GetPreviousMoveUsed
    cmp     r5, r0
    bne     CheckDisable
    
    cmp     r4, #0
    beq     TormentReturnTrue
    
    mov     r2, #145
    mov     r0, r4
    mov     r1, #2 ; File 0x12
    lsl     r2, #2 ; 580 "[Pokémon] can't use the same move twice in a row due to the torment!"
    bl      Battle::DisplayMessage
    
    mov     r0, r6
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, r4
    bl      Battle::StringParam_AddArg
    mov     r0, r4
    mov     r1, r5
    bl      Battle::StringParam_AddArg
    
TormentReturnTrue:
    add     sp, #20
    mov     r0, #1
    pop     {r4-r7, pc}
    
    
    
; Is the user locked out of using a specific move due to Disable?
CheckDisable:
    mov     r0, r6
    mov     r1, #13 ; Disable
    bl      Battle::CheckCondition
    cmp     r0, #0
    beq     CheckHealBlock
    
    mov     r0, r6
    mov     r1, #13 ; Disable
    bl      Battle::GetDisabledMove
    cmp     r5, r0
    bne     CheckHealBlock
    
    cmp     r5, #165 ; Struggle
    beq     CheckHealBlock
    
    cmp     r4, #0
    beq     DisableReturnTrue
    
    ldr     r2, =595 ; "[move] is disabled!"
    mov     r0, r4
    mov     r1, #2 ; File 0x12
    bl      Battle::DisplayMessage
    
    mov     r0, r6
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, r4
    bl      Battle::StringParam_AddArg
    
    mov     r0, r4
    mov     r1, r5
    bl      Battle::StringParam_AddArg
    
DisableReturnTrue:
    add     sp, #20
    mov     r0, #1
    pop     {r4-r7, pc}
    
    
    
; Is the user locked out of heal moves due to Heal Block?
CheckHealBlock:
    mov     r0, r6
    mov     r1, #15 ; Heal Block
    bl      Battle::CheckCondition
    cmp     r0, #0
    beq     CheckImprison
    
    mov     r0, r5
    mov     r1, #12 ; Heal move flag
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    beq     CheckImprison
    
    cmp     r4, #0
    beq     HealBlockReturnTrue
    
    ldr     r2, =890 ; "[Pokémon] can't use [move] because of Heal Block!"
    mov     r0, r4
    mov     r1, #2 ; File 0x12
    bl      Battle::DisplayMessage
    
    mov     r0, r6
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, r4
    bl      Battle::StringParam_AddArg
    
    mov     r0, r4
    mov     r1, r5
    bl      Battle::StringParam_AddArg
    
HealBlockReturnTrue:
    add     sp, #20
    mov     r0, #1
    pop     {r4-r7, pc}
    
    
    
; Is the user locked out of the move a foe has due to Imprison?
CheckImprison:
    ldr     r0, [r7, #52]
    mov     r1, #3 ; Imprison
    bl      Battle::Field_CheckEffectCore
    cmp     r0, #0
    beq     CheckGravity
    
    ldr     r0, [r7, #52]
    ldr     r1, [r7, #4]
    mov     r2, r6
    mov     r3, r5
    bl      Battle::Field_CheckImprisonCore
    cmp     r0, #0
    beq     CheckGravity
    
    cmp     r4, #0
    beq     ImprisonReturnTrue
    
    ldr     r2, =589 ; "[Pokémon] can't use the sealed [move]!"
    mov     r0, r4
    mov     r1, #2 ; File 0x12
    bl      Battle::DisplayMessage
    
    mov     r0, r6
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, r4
    bl      Battle::StringParam_AddArg
    
    mov     r0, r4
    mov     r1, r5
    bl      Battle::StringParam_AddArg
    
ImprisonReturnTrue:
    add     sp, #20
    mov     r0, #1
    pop     {r4-r7, pc}
    
    
    
; Is the user locked out of the move due to Gravity?
CheckGravity:
    ldr     r0, [r7, #52]
    mov     r1, #2 ; Gravity
    mov     r7, #2
    bl      Battle::Field_CheckEffectCore
    cmp     r0, #0
    beq     ReturnFalse
    
    mov     r0, r5
    mov     r1, #9 ; affected by Gravity move flag
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    beq     ReturnFalse
    
    cmp     r4, #0
    beq     GravityReturnTrue
    
    ldr     r2, =1086 ; "[Pokémon] can't use [move] because of gravity!"
    mov     r0, r4
    mov     r1, r7
    bl      Battle::DisplayMessage
    
    mov     r0, r6
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, r4
    bl      Battle::StringParam_AddArg
    
    mov     r0, r4
    mov     r1, r5
    bl      Battle::StringParam_AddArg
    
GravityReturnTrue:
    add     sp, #20
    mov     r0, #1
    pop     {r4-r7, pc}
    
    
    
ReturnFalse:
    mov     r0, #0
    add     sp, #20
    pop     {r4-r7, pc}