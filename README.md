# Compilador
Compilador para a disciplina de Compiladores.

##### Índice
1. [Analisador Léxico](#analisador-léxico)
   - [Identificadores](#identificadores)
   - [Palavras Reservadas](#palavras-reservadas)
   - [Símbolos Especiais](#símbolos-especiais)
   - [Comentários](#comentários)
   - [Dígitos](#dígitos)
   - [Autômatos](#autômatos)
2. [Analisador Sintático](#analisador-sintático)
   - [Definições](#definições)
   - [Programa e Bloco](#programa-e-blocos)
   - [Declarações](#declarações)
   - [Comandos](#comandos)
   - [Expressões](#expressões)

## Analisador Léxico
Abaixo estão as especificações para o analisador léxico:

#### Identificadores

Os identificadores são compostos por letras (maiúsculas ou minúsculas), números, e os caracteres _ (underscore) e @ (arroba). Eles devem iniciar necessariamente com uma letra. É crucial notar que eles não podem começar ou terminar com _ (underscore) ou @ (arroba). Além disso, se o identificador contiver o caractere @ (arroba), não pode conter o _ (underscore) simultaneamente.

Claro, aqui está a tabela com os exemplos de identificadores válidos e inválidos:

| Identificadores Válidos | Identificadores Inválidos |
|-------------------------|---------------------------|
| i                       | 1xPeso                    |
| jx                      | 24kg                      |
| soma                    | _palavras                 |
| k12aa3b                 | soma__peso                |
| z_teste                 | automato_                 |
| u_teste123a_b           | @user                     |
| u_12tes3                | case@uespi_olx            |
| s_o_mapesonota          | numero@@dois              |
| somaPeso                | teste@                    |
| K_PESO_TOTAL            | numero@dois               |
|                         |                           |
|                         |                           |

#### Palavras Reservadas

O analisador léxico deve ser capaz de reconhecer palavras reservadas, que têm significados específicos dentro da linguagem de programação.

###### Palavras reservadas que devem ser reconhecidas
|                 |             |              |            |                 |
|-----------------|-------------|--------------|------------|-----------------|
| ```principal``` | ```if```    | ```then```   | ```else``` | ```while```     |
| ```do```        | ```until``` | ```repeat``` | ```int```  | ```double```    |
| ```char```      | ```case```  | ```switch``` | ```end```  | ```procedure``` |
| ```function```  | ```for```   | ```begin```  | ```type``` | ```var```       |
| ```or```        | ```div```   | ```and```    |            |                 |

#### Símbolos Especiais

O analisador léxico deve ser capaz de reconhecer uma variedade de símbolos e operadores essenciais na análise do código-fonte.
###### Símbolos especiais que devem ser reconhecidos
```;``` ```,``` ```.``` ```+``` ```-``` ```*``` ```( ``` ```)``` ```<``` ```>``` ```:``` ```=``` ```{``` ```}``` ```:=``` ```<>``` ```<=``` ```>=``` ```/``` ```@``` ```-=```                               

#### Comentários

Os comentários de uma linha devem começar com ! (ponto de exclamação) e os comentários de múltiplas linhas devem iniciar e finalizar com !! (dois pontos de exclamação).

###### Exemplos de comentários de uma linha:
- ! Esse é um comentário de uma linha!
- ! Outro comentário de uma linha.
- @@ Outro comentário de uma linha.

###### Exemplos de comentários de múltiplas linhas:
- !! Esse é um comentário de múltiplas linhas! Isso é importante na documentação de todas as linguagens de programação.!!
- // Outro comentário de múltiplas linhas! //

#### Dígitos

O analisador léxico deve identificar números inteiros (positivos ou negativos) compostos por uma ou mais dígitos, e também reconhecer números decimais (positivos ou negativos) formados por uma parte inteira seguida por uma vírgula e uma parte decimal.

###### Exemplos de dígitos válidos:
```1``` ```12``` ```−5``` ```-54``` ```1.5``` ```254.8``` ```-123.4``` ```−12.55```

#### Autômatos
Os autômatos são essenciais na análise e reconhecimento de padrões dentro do código-fonte, permitindo a identificação de tokens, como identificadores, palavras reservadas, símbolos especiais e números.

#### Identificadores e Palavras Reservadas
<img src="./automatos/automato_identificadores_palavras_reservadas_3.png" alt="Identificadores e Palavras Reservadas"/>  

#### Dígitos
<img src="./automatos/automato_digitos.png" alt="Dígitos"/>  

#### Comentários
<img src="automatos/automato_comentarios_3.png" alt="Comentárioss"/>  

#### Símbolos Especiais
<img src="automatos/automato_simbolos_especiais.png" alt="Símbolos Especiais"/>

## Analisador Sintático
O analisador sintático é uma parte crucial de um compilador, responsável por interpretar a estrutura gramatical do código-fonte. Abaixo está a especificação do analisador sintático para esse compilador.
#### DEFINIÇÕES:

- `< e >` : não terminais
- `{ }` : pode repetir
- `[ ]` : opcional

#### PROGRAMA E BLOCOS

1. `<programa>` ➝ `principal` `<identificador>;` `<bloco>`
2. `<bloco>` ➝ `[<definicao de tipos>]`
   `[<definicao de variaveis>]`
   `[<definicao de sub-rotinas>]`
   `<comando composto>`

#### DECLARAÇÕES

3. `<definicao de tipos>` ➝ `type` `<identificador>` `=` `<tipo>` `{;` `<identificador>` `=` `<tipo>` `};`
4. `<tipo>` ➝ `<identificador>` | `int` | `boolean` | `double` | `char`
5. `<definicao de variaveis>` ➝ `var` `<lista de identificadores>` `:` `<tipo>` `{;` `<lista de identificadores>` `:` `<tipo>` `};`
6. `<lista de identificadores>` ➝ `<identificador>` `{,` `<identificador>` `}`
7. `<definicao de sub-rotinas>` ➝ `{<definicao de procedimento>; | <definição de funcao>;}`

8. `<definicao de procedimento>` ➝ `procedure` `<identificador>` `[<parametros formais>]` `;` `<bloco>`
9. `<definição de funcao>` ➝ `function` `<identificador>` `[<parametros formais>]` `:` `<identificador>` `;` `<bloco>`

(*) 10. `<parametros formais>` ➝ `(` `<lista de identificadores>` `:` `<identificador>` `{;` `<lista de identificadores>` `:` `<identificador>` `})`

#### COMANDOS

11. `<comando composto>` ➝ `begin` `<comando sem rotulo>;` `{<comando sem rotulo>;}` `end`
12. `<comando sem rotulo>` ➝ `<atribuicao>` | `<chamada de procedimento>` | `<comando condicional>` | `<comando repetitivo>`
13. `<atribuicao>` ➝ `<variavel> :=` `<expressao>`
14. `<chamada de procedimento>` ➝ `<identificador>` `[` `<lista de expressoes>` `]`

(**) 15. `<comando condicional>` ➝ `if` `<expressao>` `then` `<comando sem rotulo>` `[` `else` `<comando sem rotulo>` `]`

(**) 16. `<comando repetitivo>` ➝ `while` `<expressao>` `do` `<comando sem rotulo>`

OBS: Os comandos de entrada (read) e saída (print) estão incluídos nas chamadas de procedimentos

#### EXPRESSÕES

17. `<lista de expressoes>` ➝ `<expressao>` `{,` `<expressao>` `}`
18. `<expressao>` ➝ `<expressao simples>` `[` `<relacao>` `<expressao simples>` `]`
19. `<relacao>` ➝ `=`| `<>`| `<` | `<=` | `>` | `>=`
20. `<expressao simples>` ➝ `[+ | -]` `<termo>` `{<operador1>` `<termo>` `}`
21. `<termo>` ➝ `<fator>` `{<operador2>` `<fator>` `}`
22. `<operador1>` ➝ `+` | `-` | `or`
23. `<operador2>` ➝ `*` | `div` | `and`
24. `<fator>` ➝ `<variavel>` | `<digito>` | `<chamada de funcao>` | `(` `<expressao>` `)`
25. `<variavel>` ➝ `<identificador>`
26. `<chamada de funcao>` ➝ `<identificador>` `[` `<lista de expressoes>` `]`

(*) sem passagem de parâmetro por referência <br/>
(**) apenas um tipo de comando condicional e repetitivo será especificado