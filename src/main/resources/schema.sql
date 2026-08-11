-- Aqui garante que só exista 1 agendamento Ativo por horario
CREATE UNIQUE INDEX IF NOT EXISTS agendamento_horario_ativo
ON agendamento (horario_id)
WHERE status = 'ATIVO';
