#version 300 es
out vec4 FragColor;

in vec2 TexCoord;

// texture samplers
uniform sampler2D texture1;
uniform sampler2D texture2;

void main()
{
    FragColor = mix(texture(texture2, TexCoord), texture(texture1, TexCoord), 0.2);
}