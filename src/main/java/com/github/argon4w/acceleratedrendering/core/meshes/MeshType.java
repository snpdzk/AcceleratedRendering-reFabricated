package com.github.argon4w.acceleratedrendering.core.meshes;

public enum MeshType {

    SERVER(ServerMesh.Builder.INSTANCE),
    CLIENT(ClientMesh.Builder.INSTANCE);

    private final IMesh.Builder builder;

    MeshType(IMesh.Builder builder) {
        this.builder = builder;
    }

    public IMesh.Builder getBuilder() {
        return builder;
    }
}
