create or replace view public.v_transactions_with_final_alert_classification
with (security_invoker = on)
as

with resolved as (
  select
    te.id as transaction_id,
    te.message_id,
    te.alert_type as original_alert_type,

    tac.predicted_alert_type,
    tac.predicted_expense_treatment,
    tac.decision as classification_decision,
    tac.classification_source,
    tac.classifier_version,
    tac.confidence_score as classification_confidence_score,
    tac.needs_review as classification_needs_review,
    tac.was_domain_override,
    tac.override_rule,
    tac.reason as classification_reason,

    case
      when coalesce(te.alert_type, 'unknown') = 'unknown'
        and tac.transaction_id is not null
        and tac.needs_review = false
      then tac.predicted_alert_type

      when coalesce(te.alert_type, 'unknown') = 'unknown'
        and tac.transaction_id is not null
        and tac.needs_review = true
      then 'unknown'

      else te.alert_type
    end as final_alert_type,

    case
      when coalesce(te.alert_type, 'unknown') = 'unknown'
        and tac.transaction_id is not null
        and tac.needs_review = false
      then tac.predicted_expense_treatment

      when coalesce(te.alert_type, 'unknown') = 'unknown'
        and (
          tac.transaction_id is null
          or tac.needs_review = true
        )
      then 'needs_review'

      when te.alert_type = any (
        array[
          'bank_upi_debit'::text,
          'credit_card_purchase'::text,
          'credit_card_upi_purchase'::text,
          'debit_card_purchase'::text,
          'bank_account_debit'::text,
          'ach_account_debit'::text,
          'savings_upi_transfer'::text
        ]
      )
      then 'include_expense'

      when te.alert_type = any (
        array[
          'bank_upi_credit'::text,
          'bank_account_credit'::text
        ]
      )
      then 'exclude_income'

      -- Corrected rule
      when te.alert_type = any (
        array[
          'credit_card_payment_received'::text,
          'billpay_credit_card_payment'::text
        ]
      )
      then 'exclude_credit_card_repayment'

      when te.alert_type = any (
        array[
          'refund'::text,
          'reversal'::text
        ]
      )
      then 'reduce_expense'

      when te.alert_type = 'failed_transaction'
      then 'exclude_failed'

      when te.alert_type = any (
        array[
          'non_transaction_alert'::text,
          'mandate_setup'::text
        ]
      )
      then 'exclude_non_transaction'

      else 'needs_review'
    end as final_expense_treatment,

    te.parsed_is_transaction,
    te.parsed_amount,
    te.parsed_currency,
    te.parsed_direction,
    te.parsed_channel,
    te.parsed_txn_date,
    te.parsed_txn_date_source,
    te.account_type,
    te.account_last4,
    te.account_mask_raw,
    te.upi_vpa,
    te.upi_payee_name,
    te.upi_reference,
    te.merchant_name,
    te.counterparty_name,
    te.counterparty_type,
    te.narration_category,
    te.narration_subtype,
    te.subject_raw,
    te.body_text,
    te.message_datetime_utc,
    te.message_date,
    te.message_time,
    te.bank_name,
    te.source_pipeline,
    te.parser_version,
    te.parser_confidence,
    te.parser_reason,
    te.parse_source,
    te.used_llm,
    te.review_reason,

    te.created_at as transaction_created_at,

    tac.created_at as classification_created_at,
    tac.updated_at as classification_updated_at,
    tac.llm_processed_at

  from public.transactions_enriched te

  left join public.transaction_alert_classifications tac
    on tac.transaction_id = te.id
)

select
  transaction_id,
  message_id,
  original_alert_type,
  predicted_alert_type,
  predicted_expense_treatment,
  classification_decision,
  classification_source,
  classifier_version,
  classification_confidence_score,
  classification_needs_review,
  was_domain_override,
  override_rule,
  classification_reason,
  final_alert_type,
  final_expense_treatment,

  (
    final_expense_treatment = 'needs_review'
  ) as final_needs_review,

  parsed_is_transaction,
  parsed_amount,
  parsed_currency,
  parsed_direction,
  parsed_channel,
  parsed_txn_date,
  parsed_txn_date_source,
  account_type,
  account_last4,
  account_mask_raw,
  upi_vpa,
  upi_payee_name,
  upi_reference,
  merchant_name,
  counterparty_name,
  counterparty_type,
  narration_category,
  narration_subtype,
  subject_raw,
  body_text,
  message_datetime_utc,
  message_date,
  message_time,
  bank_name,
  source_pipeline,
  parser_version,
  parser_confidence,
  parser_reason,
  parse_source,
  used_llm,
  review_reason,
  transaction_created_at,
  classification_created_at,
  classification_updated_at,
  llm_processed_at

from resolved;