    push    {r3-r7, lr}
    mov     r4, r0
    mov     r5, r1
    
    ldr     r7, =ServerFlow.heManager
    add     r0, r4, r7
    bl      Battle::HEManager_GetUseItemNumber
    mov     r6, r0
    
    add     r0, r4, r7
    bl      Battle::HEManager_IsUsed
    cmp     r0, #FALSE
    beq     SetPrevResultTrue
    
    add     r0, r4, r7
    bl      Battle::HEManager_GetPrevResult
    lsl     r0, r0, #24
    lsr     r7, r0, #24
    b       CheckFailSkip
    

SetPrevResultTrue:
    mov     r7, #TRUE
    

CheckFailSkip:
    ldr     r0, [r5]
    lsl     r0, #7
    lsr     r0, #31 ; BHP_FailSkip
    beq     CheckAutoRemove

    cmp     r7, #FALSE
    beq     Return_Jump
    

CheckAutoRemove:
    ldr     r1, [r5]
    lsl     r0, r1, #6
    lsr     r0, r0, #31 ; BHP_AutoRemove
    beq     GetHandlerEffect
    
    ldr     r0, [r4, #0x08]
    lsl     r1, #19
    lsr     r1, #27
    bl      Battle::PokeCon_GetBattleMonConst
    bl      Battle::IsPokeFainted
    cmp     r0, #FALSE
    beq     GetHandlerEffect
    

Return_Jump:
    b       Return
    

GetHandlerEffect:
    ldrb    r3, [r5]
    cmp     r3, #HE_MAX
    bls     Switch
    
    b       SetResult
    

Switch:
    mov     r0, r4
    mov     r1, r5
    mov     r2, r6 ; not always used, but can be set here anyway
    
    #SWITCH r3
    #CASE UseHeldItem           ; 0x00
    #CASE UseHeldItemAnimation  ; 0x01
    #CASE AbilityPopupAdd       ; 0x02
    #CASE AbilityPopupRemove    ; 0x03
    #CASE Message               ; 0x04
    #CASE RecoverHP             ; 0x05
    #CASE Drain                 ; 0x06
    #CASE Damage                ; 0x07
    #CASE ChangeHP              ; 0x08
    #CASE RecoverPP             ; 0x09
    #CASE DecrementPP           ; 0x0A
    #CASE CureCondition         ; 0x0B
    #CASE AddCondition          ; 0x0C
    #CASE SetResult             ; 0x0D
    #CASE ChangeStatStage       ; 0x0E
    #CASE SetStatStage          ; 0x0F
    #CASE ResetStatStage        ; 0x10
    #CASE SetStat               ; 0x11
    #CASE ResetStat             ; 0x12
    #CASE Faint                 ; 0x13
    #CASE ChangeType            ; 0x14
    #CASE SetTurnFlag           ; 0x15
    #CASE ResetTurnFlag         ; 0x16
    #CASE SetConditionFlag      ; 0x17
    #CASE ResetConditionFlag    ; 0x18
    #CASE AddSideEffect         ; 0x19
    #CASE RemoveSideEffect      ; 0x1A
    #CASE AddFieldEffect        ; 0x1B
    #CASE RemoveFieldEffect     ; 0x1C
    #CASE ChangeWeather         ; 0x1D
    #CASE AddPosEffect          ; 0x1E
    #CASE ChangeAbility         ; 0x1F
    #CASE SetHeldItem           ; 0x20
    #CASE CheckHeldItem         ; 0x21
    #CASE ForceUseHeldItem      ; 0x22
    #CASE ConsumeItem           ; 0x23
    #CASE SwapItem              ; 0x24
    #CASE UpdateMove            ; 0x25
    #CASE SetCounter            ; 0x26
    #CASE DelayMoveDamage       ; 0x27
    #CASE QuitBattle            ; 0x28
    #CASE Switch                ; 0x29
    #CASE BatonPass             ; 0x2A
    #CASE Flinch                ; 0x2B
    #CASE Revive                ; 0x2C
    #CASE SetWeight             ; 0x2D
    #CASE ForceSwitch           ; 0x2E
    #CASE InterruptAction       ; 0x2F
    #CASE InterruptMove         ; 0x30
    #CASE SendLast              ; 0x31
    #CASE SwapPoke              ; 0x32
    #CASE Transform             ; 0x33
    #CASE IllusionBreak         ; 0x34
    #CASE GravityCheck          ; 0x35
    #CASE CancelSemiInvuln      ; 0x36
    #CASE AddAnimation          ; 0x37
    #CASE RemoveMessageWindow   ; 0x38
    #CASE ChangeForm            ; 0x39
    #CASE SetMoveAnimationID    ; 0x3A
    #CASE PlayMoveAnimation     ; 0x3B
    #CASE ChangeTerrain         ; 0x3C NEW

UseHeldItemAnimation:
    bl      Battle::Handler_UseHeldItemAnimation
    b       SetResult

AbilityPopupAdd:
    bl      Battle::Handler_AbilityPopupAdd
    b       SetResult

AbilityPopupRemove:
    bl      Battle::Handler_AbilityPopupRemove
    b       SetResult

UseHeldItem:
    bl      Battle::Handler_UseHeldItem
    b       SetResult

RecoverHP:
    bl      Battle::Handler_RecoverHP
    b       SetResult

Drain:
    bl      Battle::Handler_Drain
    b       SetResult

Damage:
    bl      Battle::Handler_Damage
    b       SetResult

ChangeHP:
    bl      Battle::Handler_ChangeHP
    b       SetResult

RecoverPP:
    bl      Battle::Handler_RecoverPP
    b       SetResult

DecrementPP:
    bl      Battle::Handler_DecrementPP
    b       SetResult

CureCondition:
    bl      Battle::Handler_CureCondition
    b       SetResult

AddCondition:
    bl      Battle::Handler_AddCondition
    b       SetResult

ChangeStatStage:
    bl      Battle::Handler_ChangeStatStage
    b       SetResult

SetStatStage:
    bl      Battle::Handler_SetStatStage
    b       SetResult

ResetStat:
    bl      Battle::Handler_ResetStat
    b       SetResult

ResetStatStage:
    bl      Battle::Handler_ResetStatStage
    b       SetResult

SetStat:
    bl      Battle::Handler_SetStat
    b       SetResult

Faint:
    bl      Battle::Handler_Faint
    b       SetResult

ChangeType:
    bl      Battle::Handler_ChangeType
    b       SetResult

Message:
    bl      Battle::Handler_Message
    b       SetResult

SetTurnFlag:
    bl      Battle::Handler_SetTurnFlag
    b       SetResult

ResetTurnFlag:
    bl      Battle::Handler_ResetTurnFlag
    b       SetResult

SetConditionFlag:
    bl      Battle::Handler_SetConditionFlag
    b       SetResult

ResetConditionFlag:
    bl      Battle::Handler_ResetConditionFlag
    b       SetResult

AddSideEffect:
    bl      Battle::Handler_AddSideEffect
    b       SetResult

RemoveSideEffect:
    bl      Battle::Handler_RemoveSideEffect
    b       SetResult

AddFieldEffect:
    bl      Battle::Handler_AddFieldEffect
    b       SetResult

ChangeWeather:
    bl      Battle::Handler_ChangeWeather
    b       SetResult

RemoveFieldEffect:
    bl      Battle::Handler_RemoveFieldEffect
    b       SetResult

AddPosEffect:
    bl      Battle::Handler_AddPosEffect
    b       SetResult

ChangeAbility:
    bl      Battle::Handler_ChangeAbility
    b       SetResult

SetHeldItem:
    bl      Battle::Handler_SetHeldItem
    b       SetResult

CheckHeldItem:
    bl      Battle::Handler_CheckHeldItem
    b       SetResult

ForceUseHeldItem:
    bl      Battle::Handler_ForceUseHeldItem
    b       SetResult

ConsumeItem:
    bl      Battle::Handler_ConsumeItem
    b       SetResult

SwapItem:
    bl      Battle::Handler_SwapItem
    b       SetResult

UpdateMove:
    bl      Battle::Handler_UpdateMove
    b       SetResult

SetCounter:
    bl      Battle::Handler_SetCounter
    b       SetResult

DelayMoveDamage:
    bl      Battle::Handler_DelayMoveDamage
    b       SetResult

QuitBattle:
    bl      Battle::Handler_QuitBattle
    b       SetResult

Switch:
    bl      Battle::Handler_Switch
    b       SetResult

BatonPass:
    bl      Battle::Handler_BatonPass
    b       SetResult

Flinch:
    bl      Battle::Handler_Flinch
    b       SetResult

Revive:
    bl      Battle::Handler_Revive
    b       SetResult

SetWeight:
    bl      Battle::Handler_SetWeight
    b       SetResult

ForceSwitch:
    bl      Battle::Handler_ForceSwitch
    b       SetResult

InterruptAction:
    bl      Battle::Handler_InterruptAction
    b       SetResult

InterruptMove:
    bl      Battle::Handler_InterruptMove
    b       SetResult

SendLast:
    bl      Battle::Handler_SendLast
    b       SetResult

SwapPoke:
    bl      Battle::Handler_SwapPoke
    b       SetResult

Transform:
    bl      Battle::Handler_Transform
    b       SetResult

IllusionBreak:
    bl      Battle::Handler_IllusionBreak
    b       SetResult

GravityCheck:
    bl      Battle::Handler_GravityCheck
    b       SetResult

CancelSemiInvuln:
    bl      Battle::Handler_CancelSemiInvuln
    b       SetResult

AddAnimation:
    bl      Battle::Handler_AddAnimation
    b       SetResult

RemoveMessageWindow:
    bl      Battle::Handler_RemoveMessageWindow
    b       SetResult

ChangeForm:
    bl      Battle::Handler_ChangeForm
    b       SetResult

SetMoveAnimationID:
    bl      Battle::Handler_SetMoveAnimationID
    b       SetResult

PlayMoveAnimation:
    bl      Battle::Handler_PlayMoveAnimation
    b       SetResult
    
; NEW
ChangeTerrain:
    bl      Battle::Handler_ChangeTerrain
; ~NEW

SetResult:
    mov     r7, r0

SetResult:
    ldr     r0, =ServerFlow.heManager
    add     r0, r4
    mov     r1, r7
    bl      Battle::HEManager_SetResult

Return:
    pop     {r3-r7, pc}